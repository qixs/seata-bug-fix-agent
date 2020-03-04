package com.qxs.seata.bug.fix.agent;

import com.qxs.seata.bug.fix.agent.handler.HandlerLoader;
import com.qxs.seata.bug.fix.agent.handler.IHandler;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class PreMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreMain.class);

    public static void premain(String agentArgs, Instrumentation inst){

        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                if(HandlerLoader.exists(className)){
                    IHandler handler = HandlerLoader.getHandler(className);
                    if(!handler.isHandled()){
                        try{
                            return handler.handle();
                        }catch (NotFoundException e){
                            LOGGER.error("处理[{}]类出错", className, e);
                        }catch (CannotCompileException e){
                            LOGGER.error("处理[{}]类出错", className, e);
                        }catch (IOException e){
                            LOGGER.error("处理[{}]类出错", className, e);
                        }
                    }
                }
                return classfileBuffer;
            }
        }, true);
    }
}
