package com.gregorune.minecartannoucer.Bookparser;

import javax.annotation.Nullable;

public final class StringStream
{
    private final String _source;
    private int _position;

    public StringStream(String source)
    {
        _source = source;
        _position = 0;
    }

    public boolean IsEndOfStream()
    { return _source.length() <= _position; }

    public boolean IsEmpty()
    { return _source.isEmpty(); }

    @Nullable
    public Character Peek()
    { return Peek(0); }
    @Nullable
    public Character Peek(int offset)
    {
        if(_position + offset < _source.length() && _position + offset >= 0)
            return _source.charAt(_position + offset);
        return null;
    }

    @Nullable
    public Character Get()
    {
        if(IsEndOfStream())
            return null;
        return _source.charAt(_position++);
    }
    public char IKnowGetNotNull() throws IndexOutOfBoundsException
    {
        return _source.charAt(_position++);
    }
}