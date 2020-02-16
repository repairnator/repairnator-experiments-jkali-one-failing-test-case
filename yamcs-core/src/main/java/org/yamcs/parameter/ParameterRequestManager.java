package org.yamcs.parameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.yamcs.ConfigurationException;
import org.yamcs.DVParameterConsumer;
import org.yamcs.InvalidIdentification;
import org.yamcs.InvalidRequestIdentification;
import org.yamcs.Processor;
import org.yamcs.alarms.AlarmServer;
import org.yamcs.protobuf.Yamcs.NamedObjectId;
import org.yamcs.utils.LoggingUtils;
import org.yamcs.xtce.DataSource;
import org.yamcs.xtce.Parameter;
import org.yamcs.xtceproc.AlarmChecker;
import org.yamcs.xtceproc.XtceTmProcessor;

import com.google.common.util.concurrent.AbstractService;

/**
 * Keeps track of which parameters are part of which subscriptions.
 * 
 * There are two types of subscriptions: - subscribe all - subscribe to a set Both types have an unique id associated
 * but different methods work with them
 * 
 */
public class ParameterRequestManager extends AbstractService implements ParameterListener {
    Logger log;
    // Maps the parameters to the request(subscription id) in which they have been asked
    private ConcurrentHashMap<Parameter, SubscriptionArray> param2RequestMap = new ConcurrentHashMap<>();

    // Maps the request (subscription id) to the consumer
    private Map<Integer, ParameterConsumer> request2ParameterConsumerMap = new ConcurrentHashMap<>();

    // these are the consumers that may update the list of parameters
    // they are delivered with priority such that in one update cycle the algorithms (or derived values) are also
    // computed
    private Map<Integer, DVParameterConsumer> request2DVParameterConsumerMap = new HashMap<>();

    // contains subscribe all
    private SubscriptionArray subscribeAll = new SubscriptionArray();

    private AlarmChecker alarmChecker;
    private Map<Class<?>, ParameterProvider> parameterProviders = new LinkedHashMap<>();

    private static AtomicInteger lastSubscriptionId = new AtomicInteger();
    public final Processor yproc;

    // if all parameter shall be subscribed/processed
    private boolean cacheAll = false;

    AlarmServer alarmServer;
    Map<DataSource, SoftwareParameterManagerIf> spm = new HashMap<>();
    ParameterCache parameterCache;
    ParameterCacheConfig cacheConfig;
    LastValueCache lastValueCache;

    /**
     * Creates a new ParameterRequestManager, configured to listen to the specified XtceTmProcessor.
     */
    public ParameterRequestManager(Processor yproc, XtceTmProcessor tmProcessor) throws ConfigurationException {
        this.yproc = yproc;
        log = LoggingUtils.getLogger(this.getClass(), yproc);
        cacheConfig = yproc.getPameterCacheConfig();
        cacheAll = cacheConfig.cacheAll;
        this.lastValueCache = yproc.getLastValueCache();

        tmProcessor.setParameterListener(this);
        addParameterProvider(tmProcessor);
        if (yproc.hasAlarmChecker()) {
            alarmChecker = new AlarmChecker(this, lastSubscriptionId.incrementAndGet());
        }
        if (yproc.hasAlarmServer()) {
            alarmServer = new AlarmServer(yproc.getInstance(), "alarms_realtime");
            alarmChecker.enableServer(alarmServer);
        }

        if (cacheConfig.enabled) {
            parameterCache = new ArrayParameterCache(yproc.getInstance(), cacheConfig);
        }
    }

    public void addParameterProvider(ParameterProvider parameterProvider) {
        if (parameterProviders.containsKey(parameterProvider.getClass())) {
            log.warn("Ignoring duplicate parameter provider of type {}", parameterProvider.getClass());
        } else {
            log.debug("Adding parameter provider: {}", parameterProvider.getClass());
            parameterProvider.setParameterListener(this);
            parameterProviders.put(parameterProvider.getClass(), parameterProvider);
        }
    }

    /**
     * This is called after all the parameter providers have been added but before the start. We subscribe to all
     * parameters if cacheAll is enabled this way we give the opportunity to the ReplayService to find out what is
     * required to retrieve from the ReplayServer
     */
    public void init() {
        if (cacheAll) {
            for (ParameterProvider prov : parameterProviders.values()) {
                prov.startProvidingAll();
            }
        } else if (alarmChecker != null) { // at least get all that have alarms
            for (Parameter p : yproc.getXtceDb().getParameters()) {
                if (p.getParameterType() != null && p.getParameterType().hasAlarm()) {
                    try {
                        subscribeToProviders(p);
                    } catch (NoProviderException e) {
                        log.warn("No provider found for parameter {} which has alarms", p.getQualifiedName());
                    }
                }
            }
        }
    }

    /**
     * subscribes to all parameters
     */
    public int subscribeAll(ParameterConsumer consumer) {
        int id = lastSubscriptionId.incrementAndGet();
        log.debug("new subscribeAll with subscriptionId {}", id);
        if (subscribeAll.isEmpty()) {
            for (ParameterProvider provider : parameterProviders.values()) {
                provider.startProvidingAll();
            }
        }
        subscribeAll.add(id);
        request2ParameterConsumerMap.put(id, consumer);
        return id;
    }

    /**
     * removes the subscription to all parameters
     * 
     * return true of the subscription has been removed or false if it was not there
     * 
     * @param subscriptionId
     * @return
     */
    public boolean unsubscribeAll(int subscriptionId) {
        return subscribeAll.remove(subscriptionId);
    }

    public int addRequest(final List<Parameter> paraList, final ParameterConsumer tpc) {

        final int id = lastSubscriptionId.incrementAndGet();
        log.debug("new request with subscriptionId {} with {} items", id, paraList.size());
        subscribeToProviders(paraList);

        for (int i = 0; i < paraList.size(); i++) {
            log.trace("adding to subscriptionID: {} item:{} ", id, paraList.get(i).getQualifiedName());
            addItemToRequest(id, paraList.get(i));
        }

        request2ParameterConsumerMap.put(id, tpc);
        return id;
    }

    /**
     * Creates a request with one parameter
     * 
     * @param para
     * @param tpc
     * @return
     */
    public int addRequest(final Parameter para, final ParameterConsumer tpc) {

        final int id = lastSubscriptionId.incrementAndGet();
        log.debug("new request with subscriptionId {} for parameter: {}", id, para.getQualifiedName());
        subscribeToProviders(para);
        addItemToRequest(id, para);
        request2ParameterConsumerMap.put(id, tpc);

        return id;
    }

    /**
     * Creates a new request with a list of parameters
     * 
     * @param paraList
     * @param dvtpc
     * @return subscription id
     */
    public int addRequest(List<Parameter> paraList, DVParameterConsumer dvtpc) {
        int id = lastSubscriptionId.incrementAndGet();
        log.debug("new request with subscriptionId {} for itemList={}", id, paraList);

        subscribeToProviders(paraList);
        for (int i = 0; i < paraList.size(); i++) {
            log.trace("adding to subscriptionID:{} item:{}", id, paraList.get(i));
            addItemToRequest(id, paraList.get(i));
        }
        request2DVParameterConsumerMap.put(id, dvtpc);
        return id;
    }

    /**
     * Create request with a given id. This is called when switching yprocessors, the id is coming from the other
     * channel.
     * 
     * @param subscriptionId
     *            - subscription id
     * @param paraList
     * @param tpc
     */
    public void addRequest(int subscriptionId, List<Parameter> paraList, ParameterConsumer tpc) {
        subscribeToProviders(paraList);
        for (int i = 0; i < paraList.size(); i++) {
            log.trace("creating subscriptionID:{} with item:{}", subscriptionId, paraList.get(i));
            addItemToRequest(subscriptionId, paraList.get(i));
        }
        request2ParameterConsumerMap.put(subscriptionId, tpc);
    }

    /**
     * Add items to an request id.
     * 
     * @param subscriptionId
     * @param para
     */
    public void addItemsToRequest(final int subscriptionId, final Parameter para) throws InvalidRequestIdentification {
        log.debug("adding to subscriptionID {}: items: {} ", subscriptionId, para.getName());
        final ParameterConsumer consumer = request2ParameterConsumerMap.get(subscriptionId);
        if ((consumer == null) && !request2DVParameterConsumerMap.containsKey(subscriptionId)
                && alarmChecker != null && alarmChecker.getSubscriptionId() != subscriptionId) {
            log.error(" addItemsToRequest called with an invalid subscriptionId={}\n current subscr:\n{}dv "
                    + "subscr:\n {}", subscriptionId, request2ParameterConsumerMap, request2DVParameterConsumerMap);
            throw new InvalidRequestIdentification("no such subscriptionID", subscriptionId);
        }
        subscribeToProviders(para);
        addItemToRequest(subscriptionId, para);
    }

    /**
     * Add items to a subscription.
     * 
     * @param subscriptionId
     * @param paraList
     *            list of parameters that are added to the subscription
     * @throws InvalidRequestIdentification
     */
    public void addItemsToRequest(final int subscriptionId, final List<Parameter> paraList)
            throws InvalidRequestIdentification {
        log.debug("adding to subscriptionID {}: {} items ", subscriptionId, paraList.size());
        final ParameterConsumer consumer = request2ParameterConsumerMap.get(subscriptionId);
        if ((consumer == null) && !request2DVParameterConsumerMap.containsKey(subscriptionId)) {
            log.error(" addItemsToRequest called with an invalid subscriptionId={}\n current "
                    + "subscr:\ndv subscr:\n{}", subscriptionId, request2ParameterConsumerMap,
                    request2DVParameterConsumerMap);
            throw new InvalidRequestIdentification("no such subscriptionID", subscriptionId);
        }
        subscribeToProviders(paraList);
        for (int i = 0; i < paraList.size(); i++) {
            addItemToRequest(subscriptionId, paraList.get(i));
        }
    }

    /**
     * Removes a parameter from a rquest. If it is not part of the request, the operation will have no effect.
     * 
     * @param subscriptionID
     * @param param
     */
    public void removeItemsFromRequest(int subscriptionID, Parameter param) {
        removeItemFromRequest(subscriptionID, param);
    }

    /**
     * Removes a list of parameters from a request. Any parameter specified that is not in the subscription will be
     * ignored.
     * 
     * @param subscriptionID
     * @param paraList
     */
    public void removeItemsFromRequest(int subscriptionID, List<Parameter> paraList) {
        for (int i = 0; i < paraList.size(); i++) {
            removeItemFromRequest(subscriptionID, paraList.get(i));
        }
    }

    /**
     * Adds a new item to an existing request. There is no check if the item is already there, so there can be
     * duplicates (observed in the CGS CIS). This call also works with a new id
     * 
     * @param id
     * @param para
     */
    private void addItemToRequest(int id, Parameter para) {
        if (!param2RequestMap.containsKey(para)) {
            // this parameter is not requested by any other request
            if (param2RequestMap.putIfAbsent(para, new SubscriptionArray()) == null) {
                if (alarmChecker != null) {
                    alarmChecker.parameterSubscribed(para);
                }
            }
        }
        SubscriptionArray al_req = param2RequestMap.get(para);
        al_req.add(id);
    }

    private void removeItemFromRequest(int subscriptionId, Parameter para) {
        if (param2RequestMap.containsKey(para)) { // is there really any request associated to this parameter?
            SubscriptionArray al_req = param2RequestMap.get(para);
            // remove the subscription from the list of this parameter
            if (al_req.remove(subscriptionId)) {
                /*
                 * Don't remove the al_req from the map and
                 * don't ask provider to stop providing
                 * because it is not thread safe (maybe another thread just asked to start providing after seeing that
                 * the list is empty
                 * if(al_req.isEmpty()) { //nobody wants this parameter anymore
                 * if(!cacheAll) provider.stopProviding(para);
                 * }
                 */
            } else {
                log.warn("parameter removal requested for {}but not part of subscription {}", para, subscriptionId);
            }
        } else {
            log.warn("parameter removal requested for {} but not subscribed", para);
        }
    }

    /**
     * Removes all the items from this subscription and returns them into an List. The result is usually used in the
     * TelemetryImpl to move this subscription to a different ParameterRequestManager
     */
    public List<Parameter> removeRequest(int subscriptionId) {
        log.debug("removing request for subscriptionId {}", subscriptionId);
        // It's a bit annoying that we have to loop through all the parameters to find the ones that
        // are relevant for this request. We could keep track of an additional map.
        ArrayList<Parameter> result = new ArrayList<>();
        // loop through all the parameter definitions
        // find all the subscriptions with the requested subscriptionId and add their corresponding
        // itemId to the list.
        Iterator<Map.Entry<Parameter, SubscriptionArray>> it = param2RequestMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Parameter, SubscriptionArray> m = it.next();
            Parameter param = m.getKey();
            SubscriptionArray al_req = m.getValue();
            if (al_req.remove(subscriptionId)) {
                result.add(param);
            }
            if (al_req.isEmpty()) { // nobody wants this parameter anymore
                /*
                 * if(!cacheAll) { commented out because not thread safe
                 * getProvider(param).stopProviding(param);
                 * }
                 */
            }
        }
        request2ParameterConsumerMap.remove(subscriptionId);
        request2DVParameterConsumerMap.remove(subscriptionId);
        return result;
    }

    private void subscribeToProviders(Parameter param) throws NoProviderException {
        if (cacheAll) {
            return;
        }
        boolean providerFound = false;

        for (ParameterProvider provider : parameterProviders.values()) {
            if (provider.canProvide(param)) {
                providerFound = true;
                provider.startProviding(param);
            }
        }
        if (!providerFound) {
            throw new NoProviderException("No provider found for " + param);
        }

    }

    private void subscribeToProviders(List<Parameter> itemList) {
        if (cacheAll) {
            return;
        }
        for (int i = 0; i < itemList.size(); i++) {
            subscribeToProviders(itemList.get(i));
        }
    }

    /**
     * returns a parameter based on fully qualified name
     * 
     * @param fqn
     * @return
     * @throws InvalidIdentification
     */
    public Parameter getParameter(String fqn) throws InvalidIdentification {
        return getParameter(NamedObjectId.newBuilder().setName(fqn).build());
    }

    /**
     * @param paraId
     * @return the corresponding parameter definition for a IntemIdentification
     * @throws InvalidIdentification
     *             in case no provider knows of this parameter.
     */
    public Parameter getParameter(NamedObjectId paraId) throws InvalidIdentification {
        for (ParameterProvider provider : parameterProviders.values()) {
            if (provider.canProvide(paraId)) {
                return provider.getParameter(paraId);
            }
        }
        throw new InvalidIdentification(paraId);
    }

    @Override
    public void update(Collection<ParameterValue> params) {
        log.trace("ParamRequestManager.updateItems with {} parameters", params.size());

        lastValueCache.update(params);
        // maps subscription id to a list of (value,id) to be delivered for that subscription
        HashMap<Integer, ArrayList<ParameterValue>> delivery = new HashMap<>();

        // so first we add to the delivery the parameters just received
        updateDelivery(delivery, params);

        // then if the delivery updates some of the parameters required by the derived values
        // compute the derived values
        for (Map.Entry<Integer, DVParameterConsumer> entry : request2DVParameterConsumerMap.entrySet()) {
            Integer subscriptionId = entry.getKey();
            if (delivery.containsKey(subscriptionId)) {
                List<ParameterValue> pvList = entry.getValue().updateParameters(subscriptionId,
                        delivery.get(subscriptionId));
                lastValueCache.update(pvList);
                updateDelivery(delivery, pvList);
            }
        }

        // and finally deliver the delivery :)
        for (Map.Entry<Integer, ArrayList<ParameterValue>> entry : delivery.entrySet()) {
            Integer subscriptionId = entry.getKey();
            if (request2DVParameterConsumerMap.containsKey(subscriptionId)) {
                continue;
            }
            if (alarmChecker != null && alarmChecker.getSubscriptionId() == subscriptionId) {
                continue;
            }

            ArrayList<ParameterValue> al = entry.getValue();
            ParameterConsumer consumer = request2ParameterConsumerMap.get(subscriptionId);
            if (consumer == null) {
                log.warn("subscriptionId {} appears in the delivery list, but there is no consumer for it",
                        subscriptionId);
            } else {
                consumer.updateItems(subscriptionId, al);
            }
        }
    }

    /**
     * adds the passed parameters to the delivery
     * 
     * @param delivery
     * @param params
     */
    private void updateDelivery(HashMap<Integer, ArrayList<ParameterValue>> delivery,
            Collection<ParameterValue> params) {
        if (params == null) {
            return;
        }

        for (Iterator<ParameterValue> it = params.iterator(); it.hasNext();) {
            ParameterValue pv = it.next();
            Parameter pDef = pv.getParameter();
            SubscriptionArray cowal = param2RequestMap.get(pDef);
            // now walk through the requests and add this item to their delivery list
            if (cowal == null) {
                continue;
            }

            for (int s : cowal.getArray()) {
                ArrayList<ParameterValue> al = delivery.get(s);
                if (al == null) {
                    al = new ArrayList<>();
                    delivery.put(s, al);
                }
                al.add(pv);
            }
        }

        // update the subscribeAll subscriptions
        for (int id : subscribeAll.getArray()) {
            ArrayList<ParameterValue> al = delivery.get(id);

            if (al == null) {
                al = new ArrayList<>();
                delivery.put(id, al);
            }

            for (ParameterValue pv : params) {
                al.add(pv);
            }
        }
        if (alarmChecker != null) {
            try {
                alarmChecker.performAlarmChecking(params);
            } catch (Exception e) {
                log.error("Error when performing alarm checking ", e);
            }
        }

        if (parameterCache != null) {
            parameterCache.update(params);
        }
    }

    /**
     * 
     * @return the SoftwareParameterManager associated to the DataSource or null if not configured
     */
    public SoftwareParameterManagerIf getSoftwareParameterManager(DataSource ds) {
        return spm.get(ds);
    }

    @SuppressWarnings("unchecked")
    public <T extends ParameterProvider> T getParameterProvider(Class<T> type) {
        return (T) parameterProviders.get(type);
    }

    public AlarmChecker getAlarmChecker() {
        return alarmChecker;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Current Subscription list:\n");
        for (Parameter param : param2RequestMap.keySet()) {
            sb.append(param);
            sb.append("requested by [");
            SubscriptionArray al_req = param2RequestMap.get(param);
            for (int id : al_req.getArray()) {
                sb.append(id);
            }
            sb.append("]\n");
        }
        return sb.toString();
    }

    public AlarmServer getAlarmServer() {
        return alarmServer;
    }

    public boolean hasParameterCache() {
        return parameterCache != null;
    }

    /**
     * Returns the last known value for each parameter.
     * 
     * @param plist
     * @return
     */
    public List<ParameterValue> getValuesFromCache(List<Parameter> plist) {
        List<ParameterValue> al = new ArrayList<>(plist.size());
        for (Parameter p : plist) {
            ParameterValue pv = lastValueCache.getValue(p);
            if (pv != null) {
                al.add(pv);
            }
        }
        return al;
    }

    /**
     * Get the last value from cache for a specific parameters
     * 
     * @param param
     * @return
     */
    public ParameterValue getLastValueFromCache(Parameter param) {
        return lastValueCache.getValue(param);
    }

    /**
     * Get all the values from cache for a specific parameters
     * 
     * The parameter are returned in descending order (newest parameter is returned first). Note that you can only all
     * this function if the {@link #hasParameterCache()} returns true.
     * 
     * @param param
     * @return
     */
    public List<ParameterValue> getValuesFromCache(Parameter param) {
        return parameterCache.getAllValues(param);
    }

    public ParameterCache getParameterCache() {
        return parameterCache;
    }

    public Object getXtceDb() {
        return yproc.getXtceDb();
    }

    @Override
    protected void doStart() {
        if (alarmServer != null) {
            alarmServer.startAsync();
        }
        notifyStarted();
    }

    @Override
    protected void doStop() {
        if (alarmServer != null) {
            alarmServer.stopAsync();
        }
        notifyStopped();
    }

    public LastValueCache getLastValueCache() {
        return lastValueCache;
    }

    /**
     * Register a {@link SoftwareParameterManagerIf} for the given {@link DataSource}.
     * Throws an {@link IllegalStateException} if there is already registered a parameter manager for this data source.
     * 
     * @param ds
     * @param swParameterManager
     */
    public void addSoftwareParameterManager(DataSource ds, SoftwareParameterManagerIf swParameterManager) {
        if(spm.containsKey(ds)) {
            throw new IllegalStateException("There is already a soft parameter manager for "+ds);
        }
        spm.put(ds, swParameterManager);
    }
}
