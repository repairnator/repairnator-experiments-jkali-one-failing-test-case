package org.yamcs.web.rest.mdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yamcs.protobuf.Mdb.ParameterTypeInfo;
import org.yamcs.protobuf.Rest.ListParameterTypeInfoResponse;
import org.yamcs.web.HttpException;
import org.yamcs.web.rest.RestHandler;
import org.yamcs.web.rest.RestRequest;
import org.yamcs.web.rest.Route;
import org.yamcs.web.rest.mdb.XtceToGpbAssembler.DetailLevel;
import org.yamcs.xtce.NameDescription;
import org.yamcs.xtce.ParameterType;
import org.yamcs.xtce.XtceDb;
import org.yamcs.xtceproc.XtceDbFactory;

/**
 * Handles incoming requests related to parameter type info from the MDB
 */
public class MDBParameterTypeRestHandler extends RestHandler {
    final static Logger log = LoggerFactory.getLogger(MDBParameterTypeRestHandler.class);

    @Route(path = "/api/mdb/:instance/parameter-types", method = "GET")
    @Route(path = "/api/mdb/:instance/parameter-types/:name*", method = "GET")
    public void getParameterType(RestRequest req) throws HttpException {
        if (req.hasRouteParam("name")) {
            getParameterTypeInfo(req);
        } else {
            listParameterTypes(req);
        }
    }

    private void getParameterTypeInfo(RestRequest req) throws HttpException {
        String instance = verifyInstance(req, req.getRouteParam("instance"));

        XtceDb mdb = XtceDbFactory.getInstance(instance);
        ParameterType p = verifyParameterType(req, mdb, req.getRouteParam("name"));

        ParameterTypeInfo pinfo = XtceToGpbAssembler.toParameterTypeInfo(p, DetailLevel.FULL);
        completeOK(req, pinfo);
    }

    private void listParameterTypes(RestRequest req) throws HttpException {
        String instance = verifyInstance(req, req.getRouteParam("instance"));
        XtceDb mdb = XtceDbFactory.getInstance(instance);

        // Should eventually be replaced in a generic mdb search operation
        NameDescriptionSearchMatcher matcher = null;
        if (req.hasQueryParameter("q")) {
            matcher = new NameDescriptionSearchMatcher(req.getQueryParameter("q"));
        }

        boolean recurse = req.getQueryParameterAsBoolean("recurse", false);
        boolean details = req.getQueryParameterAsBoolean("details", false);

        List<ParameterType> matchedTypes = new ArrayList<>();
        if (req.hasQueryParameter("namespace")) {
            String namespace = req.getQueryParameter("namespace");
            for (ParameterType t : mdb.getParameterTypes()) {
                if (matcher != null && !matcher.matches((NameDescription) t)) {
                    continue;
                }

                String alias = ((NameDescription) t).getAlias(namespace);
                if (alias != null || (recurse && ((NameDescription) t).getQualifiedName().startsWith(namespace))) {
                    matchedTypes.add(t);
                }
            }
        } else { // List all
            for (ParameterType t : mdb.getParameterTypes()) {
                if (matcher != null && !matcher.matches((NameDescription) t)) {
                    continue;
                }
                matchedTypes.add(t);
            }
        }

        Collections.sort(matchedTypes, (t1, t2) -> {
            return ((NameDescription) t1).getQualifiedName().compareTo(((NameDescription) t2).getQualifiedName());
        });

        int pos = req.getQueryParameterAsInt("pos", 0);
        int limit = req.getQueryParameterAsInt("limit", 100);
        if (pos > 0) {
            matchedTypes = matchedTypes.subList(pos, matchedTypes.size());
        }
        if (limit < matchedTypes.size()) {
            matchedTypes = matchedTypes.subList(0, limit);
        }

        ListParameterTypeInfoResponse.Builder responseb = ListParameterTypeInfoResponse.newBuilder();
        for (ParameterType t : matchedTypes) {
            responseb.addType(
                    XtceToGpbAssembler.toParameterTypeInfo(t, details ? DetailLevel.FULL : DetailLevel.SUMMARY));
        }
        completeOK(req, responseb.build());
    }
}
