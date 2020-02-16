package org.yamcs.web.rest.archive;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yamcs.api.MediaType;
import org.yamcs.parameter.ParameterValueWithId;
import org.yamcs.protobuf.Pvalue.ParameterData;
import org.yamcs.protobuf.Pvalue.ParameterValue;
import org.yamcs.protobuf.Pvalue.TimeSeries;
import org.yamcs.protobuf.Yamcs.EndAction;
import org.yamcs.protobuf.Yamcs.NamedObjectId;
import org.yamcs.protobuf.Yamcs.ParameterReplayRequest;
import org.yamcs.protobuf.Yamcs.ReplayRequest;
import org.yamcs.protobuf.Yamcs.ReplaySpeed;
import org.yamcs.protobuf.Yamcs.ReplaySpeed.ReplaySpeedType;
import org.yamcs.utils.ParameterFormatter;
import org.yamcs.utils.TimeEncoding;
import org.yamcs.web.BadRequestException;
import org.yamcs.web.HttpException;
import org.yamcs.web.InternalServerErrorException;
import org.yamcs.web.rest.RestHandler;
import org.yamcs.web.rest.RestParameterReplayListener;
import org.yamcs.web.rest.RestReplayListener;
import org.yamcs.web.rest.RestRequest;
import org.yamcs.web.rest.archive.RestDownsampler.Sample;
import org.yamcs.xtce.FloatParameterType;
import org.yamcs.xtce.IntegerParameterType;
import org.yamcs.xtce.Parameter;
import org.yamcs.xtce.ParameterType;
import org.yamcs.xtce.XtceDb;
import org.yamcs.xtceproc.XtceDbFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;

/**
 * provides parameters by doing replays. In general, if possible, avoid replays and use the parameter archive (much
 * faster)
 * 
 * @author nm
 *
 */
public class ArchiveParameterReplayRestHandler extends RestHandler {
    private static final Logger log = LoggerFactory.getLogger(ArchiveParameterReplayRestHandler.class);

    /**
     * A series is a list of samples that are determined in one-pass while processing a stream result. Final API
     * unstable.
     */
    public void getParameterSamples(RestRequest req) throws HttpException {
        String instance = verifyInstance(req, req.getRouteParam("instance"));

        XtceDb mdb = XtceDbFactory.getInstance(instance);
        Parameter p = verifyParameter(req, mdb, req.getRouteParam("name"));

        ParameterType ptype = p.getParameterType();
        if ((ptype != null) && (!(ptype instanceof FloatParameterType) && !(ptype instanceof IntegerParameterType))) {
            throw new BadRequestException(
                    "Only integer or float parameters can be sampled. Got " + ptype.getTypeAsString());
        }

        ReplayRequest.Builder rr = ReplayRequest.newBuilder().setEndAction(EndAction.QUIT);
        rr.setSpeed(ReplaySpeed.newBuilder().setType(ReplaySpeedType.AFAP));
        NamedObjectId id = NamedObjectId.newBuilder().setName(p.getQualifiedName()).build();
        rr.setParameterRequest(ParameterReplayRequest.newBuilder().addNameFilter(id));

        long defaultStop = TimeEncoding.getWallclockTime();
        long defaultStart = defaultStop - (1000 * 60 * 60); // 1 hour

        rr.setStart(req.getQueryParameterAsDate("start", defaultStart));
        rr.setStop(req.getQueryParameterAsDate("stop", defaultStop));
        int sampleCount = req.getQueryParameterAsInt("count", 500);

        RestDownsampler sampler = new RestDownsampler(rr.getStart(), rr.getStop(), sampleCount);

        RestReplays.replay(instance, req.getUser(), rr.build(), new RestReplayListener() {
            @Override
            public void onParameterData(List<ParameterValueWithId> params) {
                for (ParameterValueWithId pvalid : params) {
                    sampler.process(pvalid.getParameterValue());
                }
            }

            @Override
            public void replayFinished() {
                TimeSeries.Builder series = TimeSeries.newBuilder();
                for (Sample s : sampler.collect()) {
                    series.addSample(ArchiveHelper.toGPBSample(s));
                }
                completeOK(req, series.build());
            }

            @Override
            public void replayFailed(Throwable t) {
                completeWithError(req, new InternalServerErrorException(t));
            }
        });
    }

    public void listParameterHistory(RestRequest req) throws HttpException {
        String instance = verifyInstance(req, req.getRouteParam("instance"));

        XtceDb mdb = XtceDbFactory.getInstance(instance);
        String pathName = req.getRouteParam("name");

        NameDescriptionWithId<Parameter> p = verifyParameterWithId(req, mdb, pathName);

        long pos = req.getQueryParameterAsLong("pos", 0);
        int limit = req.getQueryParameterAsInt("limit", 100);
        boolean noRepeat = req.getQueryParameterAsBoolean("norepeat", false);

        ReplayRequest rr = ArchiveHelper.toParameterReplayRequest(req, p.getRequestedId(), true);

        if (req.asksFor(MediaType.CSV)) {
            ByteBuf buf = req.getChannelHandlerContext().alloc().buffer();
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new ByteBufOutputStream(buf)))) {
                List<NamedObjectId> idList = Arrays.asList(p.getRequestedId());
                ParameterFormatter csvFormatter = new ParameterFormatter(bw, idList);
                limit++; // Allow one extra line for the CSV header
                RestParameterReplayListener replayListener = new RestParameterReplayListener(pos, limit, req) {
                    @Override
                    public void onParameterData(List<ParameterValueWithId> params) {
                        try {
                            List<ParameterValue> pvlist = new ArrayList<>();
                            for (ParameterValueWithId pvalid : params) {
                                pvlist.add(pvalid.toGbpParameterValue());
                            }
                            csvFormatter.writeParameters(pvlist);
                        } catch (IOException e) {
                            log.error("Error while writing parameter line", e);
                            completeWithError(req, new InternalServerErrorException(e));
                        }
                    }

                    @Override
                    public void replayFinished() {
                        completeOK(req, MediaType.CSV, buf);
                    }
                };
                replayListener.setNoRepeat(noRepeat);
                RestReplays.replay(instance, req.getUser(), rr, replayListener);

            } catch (IOException e) {
                throw new InternalServerErrorException(e);
            }

        } else {
            ParameterData.Builder resultb = ParameterData.newBuilder();
            RestParameterReplayListener replayListener = new RestParameterReplayListener(pos, limit, req) {
                @Override
                public void onParameterData(List<ParameterValueWithId> params) {
                    for (ParameterValueWithId pvalid : params) {
                        resultb.addParameter(pvalid.toGbpParameterValue());
                    }
                }

                @Override
                public void replayFinished() {
                    completeOK(req, resultb.build());
                }
            };
            replayListener.setNoRepeat(noRepeat);
            RestReplays.replay(instance, req.getUser(), rr, replayListener);
        }
    }
}
