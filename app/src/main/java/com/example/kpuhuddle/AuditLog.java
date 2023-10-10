package com.example.kpuhuddle;

public class AuditLog
{
    String logID, logUser, logDesc, logTimeStamp;

    public AuditLog()
    {}

    public AuditLog(String logID, String logUser, String logDesc, String logTimeStamp)
    {
        this.logID = logID;
        this.logUser = logUser;
        this.logDesc = logDesc;
        this.logTimeStamp = logTimeStamp;
    }

    public String getLogID()
    {
        return logID;
    }

    public void setLogID(String logID)
    {
        this.logID = logID;
    }

    public String getLogUser()
    {
        return logUser;
    }

    public void setLogUser(String logUser)
    {
        this.logUser = logUser;
    }

    public String getLogDesc()
    {
        return logDesc;
    }

    public void setLogDesc(String logDesc)
    {
        this.logDesc = logDesc;
    }

    public String getLogTimeStamp()
    {
        return logTimeStamp;
    }

    public void setLogTimeStamp(String logTimeStamp)
    {
        this.logTimeStamp = logTimeStamp;
    }
}
