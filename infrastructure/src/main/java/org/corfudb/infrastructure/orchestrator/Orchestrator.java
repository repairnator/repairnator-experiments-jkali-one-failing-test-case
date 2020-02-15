package org.corfudb.infrastructure.orchestrator;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.corfudb.infrastructure.IServerRouter;
import org.corfudb.protocols.wireprotocol.CorfuMsgType;
import org.corfudb.protocols.wireprotocol.CorfuPayloadMsg;
import org.corfudb.protocols.wireprotocol.orchestrator.Action;
import org.corfudb.protocols.wireprotocol.orchestrator.ActionStatus;
import org.corfudb.protocols.wireprotocol.orchestrator.CreateRequest;
import org.corfudb.protocols.wireprotocol.orchestrator.CreateWorkflowResponse;
import org.corfudb.protocols.wireprotocol.orchestrator.IWorkflow;
import org.corfudb.protocols.wireprotocol.orchestrator.OrchestratorMsg;
import org.corfudb.protocols.wireprotocol.orchestrator.OrchestratorResponse;
import org.corfudb.protocols.wireprotocol.orchestrator.QueryRequest;
import org.corfudb.protocols.wireprotocol.orchestrator.QueryResponse;
import org.corfudb.protocols.wireprotocol.orchestrator.Response;
import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.runtime.view.Layout;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

/**
 * The orchestrator is a stateless service that runs on all management servers and its purpose
 * is to execute workflows. A workflow defines multiple smaller actions that must run in order
 * that is specified by Workflow.getActions() to achieve a bigger goal. For example, growing the
 * cluster. Initiated through RPC, the orchestrator will create a workflow instance and attempt
 * to execute all its actions.
 *
 * Created by Maithem on 10/25/17.
 */

@Slf4j
public class Orchestrator {

    final Callable<CorfuRuntime> getRuntime;
    final BiMap<UUID, String> activeWorkflows = Maps.synchronizedBiMap(HashBiMap.create());

    public Orchestrator(@Nonnull Callable<CorfuRuntime> runtime) {
        this.getRuntime = runtime;
    }

    public void handle(@Nonnull CorfuPayloadMsg<OrchestratorMsg> msg,
                       @Nonnull ChannelHandlerContext ctx,
                       @Nonnull IServerRouter r) {

        OrchestratorMsg orchReq = msg.getPayload();

        switch (orchReq.getRequest().getType()) {
            case QUERY:
                query(msg, ctx, r);
                break;
            case ADD_NODE:
                dispatch(msg, ctx, r);
                break;
            default:
                log.error("handle: Unknown request type {}", orchReq.getRequest().getType());
        }
    }

    /**
     *
     * Query a workflow id.
     *
     * Queries a workflow id and returns true if this orchestrator is still
     * executing the workflow, otherwise return false.
     *
     * @param msg corfu message containing the query request
     * @param ctx netty ChannelHandlerContext
     * @param r   server router
     */
    void query(CorfuPayloadMsg<OrchestratorMsg> msg, ChannelHandlerContext ctx, IServerRouter r) {
        QueryRequest req = (QueryRequest) msg.getPayload().getRequest();

        Response resp;
        if (activeWorkflows.containsKey(req.getId())) {
            resp = new QueryResponse(true);
            log.trace("handleQuery: returning active for id {}", req.getId());
        } else {
            resp = new QueryResponse(false);
            log.trace("handleQuery: returning not active for id {}", req.getId());
        }

        r.sendResponse(ctx, msg, CorfuMsgType.ORCHESTRATOR_RESPONSE
                .payloadMsg(new OrchestratorResponse(resp)));
    }

    /**
     *
     * Dispatch a workflow create request.
     *
     * Create and start a workflow on this orchestrator, if there is
     * an existing workflow that is executing on the same endpoint,
     * then just return the corresponding workflow id. Dispatch is the only
     * place where workflows are created based on reading activeWorkflows
     * and therefore needs to be synchronized to prevent launching multiple
     * workflows for the same endpoint concurrently.
     *
     * @param msg corfu message containing the create workflow request
     * @param ctx netty ChannelHandlerContext
     * @param r   server router
     */
    synchronized void dispatch(CorfuPayloadMsg<OrchestratorMsg> msg,
                               ChannelHandlerContext ctx, IServerRouter r) {
        CreateRequest req = (CreateRequest) msg.getPayload().getRequest();

        UUID id = activeWorkflows.inverse().get(req.getEndpoint());
        if (id != null) {
            // A workflow is already executing for this endpoint, return
            // existing workflow id.
            OrchestratorResponse resp = new OrchestratorResponse(
                    new CreateWorkflowResponse(id));
            r.sendResponse(ctx, msg, CorfuMsgType.ORCHESTRATOR_RESPONSE
                    .payloadMsg(resp));
            return;
        } else {
            // Create a new workflow for this endpoint and return a new workflow id
            IWorkflow workflow = req.getWorkflow();
            activeWorkflows.put(workflow.getId(), req.getEndpoint());

            CompletableFuture.runAsync(() -> {
                run(workflow);
            });

            OrchestratorResponse resp = new OrchestratorResponse(new CreateWorkflowResponse(workflow.getId()));
            r.sendResponse(ctx, msg, CorfuMsgType.ORCHESTRATOR_RESPONSE
                    .payloadMsg(resp));
        }
    }

    /**
     * Run a particular workflow, which entails executing all its defined
     * actions
     *
     * @param workflow instance to run
     */
    void run(@Nonnull IWorkflow workflow) {
        CorfuRuntime rt = null;
        try {
            Layout currLayout = getRuntime.call().layout.get();
            String servers = String.join(",", currLayout.getLayoutServers());
            rt = new CorfuRuntime(servers)
                    .setCacheDisabled(true)
                    .setLoadSmrMapsAtConnect(false)
                    .connect();

            log.info("run: Started workflow {} id {}", workflow.getName(), workflow.getId());
            long workflowStart = System.currentTimeMillis();
            for (Action action : workflow.getActions()) {

                log.debug("run: Started action {} for workflow {}", action.getName(), workflow.getId());
                long actionStart = System.currentTimeMillis();
                action.execute(rt);
                long actionEnd = System.currentTimeMillis();
                log.info("run: finished action {} for workflow {} in {} ms",
                        action.getName(), workflow.getId(), actionEnd - actionStart);

                if (action.getStatus() != ActionStatus.COMPLETED) {
                    log.error("run: Failed to execute action {} for workflow {}, status {}, ",
                            action.getName(), workflow.getId(),
                            action.getStatus());
                    return;
                }
            }

            long workflowEnd = System.currentTimeMillis();
            log.info("run: Completed workflow {} in {} ms", workflow.getId(), workflowEnd - workflowStart);
        } catch (Exception e) {
            log.error("run: Encountered an error while running workflow {}", workflow.getId(), e);
            return;
        } finally {
            activeWorkflows.remove(workflow.getId());
            log.debug("run: removed {} from {}", workflow.getId(), activeWorkflows);
            if (rt != null) {
                rt.shutdown();
            }
        }
    }
}
