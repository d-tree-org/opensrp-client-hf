package org.smartregister.hf.domain;

import org.opensrp.api.domain.BaseEntity;

import java.util.Map;

public class Entity extends BaseEntity {
    private String baseEntityId;
    private String familyId;

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }
}
