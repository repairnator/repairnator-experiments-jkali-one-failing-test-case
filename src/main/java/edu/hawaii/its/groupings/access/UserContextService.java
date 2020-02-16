package edu.hawaii.its.groupings.access;

public interface UserContextService {
    public abstract User getCurrentUser();

    public abstract String getCurrentUsername();

    public abstract Long getCurrentUhuuid();

    public void setCurrentUhuuid(Long uhuuid);
}