package com.longmaple.ttmall.licensesvr.repository;

import com.longmaple.ttmall.licensesvr.model.Organization;

public interface OrganizationRedisRepository {
    void saveOrganization(Organization org);
    void updateOrganization(Organization org);
    void deleteOrganization(String organizationId);
    Organization findOrganization(String organizationId);
}
