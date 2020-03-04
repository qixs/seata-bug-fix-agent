package com.qxs.seata.bug.fix.agent.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class HandlerLoader {

    private static final Map<String, IHandler> HANDLER_MAP;

    static {
        Map<String, IHandler> handlerMap = new HashMap<String, IHandler>();
        handlerMap.put(ZookeeperConfigurationHandler.getClassName(), new ZookeeperConfigurationHandler());
        HANDLER_MAP = Collections.unmodifiableMap(handlerMap);
    }

    public static boolean exists(String className){
        return HANDLER_MAP.containsKey(className);
    }

    public static IHandler getHandler(String className){
        return HANDLER_MAP.get(className);
    }
}
