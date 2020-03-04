package com.qxs.seata.bug.fix.agent.handler;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import java.io.IOException;

public interface IHandler {

    boolean isHandled();

    byte[] handle() throws NotFoundException, CannotCompileException, IOException;
}
