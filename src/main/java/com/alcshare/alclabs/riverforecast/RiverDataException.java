package com.alcshare.alclabs.riverforecast;

public class RiverDataException extends Exception
{
    public RiverDataException(String message)
    {
        super(message);
    }

    public RiverDataException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
