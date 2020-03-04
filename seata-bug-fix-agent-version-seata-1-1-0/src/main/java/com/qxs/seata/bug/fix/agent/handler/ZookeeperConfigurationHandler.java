package com.qxs.seata.bug.fix.agent.handler;

import javassist.*;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ZookeeperConfigurationHandler extends AbstractHandler implements IHandler{

    private static final String CLASS_NAME = "io.seata.config.zk.ZookeeperConfiguration";
    private static final String ZK_SERIALIZER_CLASS_NAME = "io.seata.config.zk.ZkSerializer";
    private static final String ZK_SERIALIZER_INTERFACE_CLASS_NAME = "org.I0Itec.zkclient.serialize.ZkSerializer";

    private static boolean handled = false;

    private static final String SET_ZK_SERIALIZER_CODE_PATH = "/codes/zookeeperConfigurationHandler/setZkSerializer";
    private static final String SET_ZK_SERIALIZER_CODE;

    private static final String ZK_SERIALIZER_SERIALIZE_CODE_PATH = "/codes/zookeeperConfigurationHandler/zkSerializer/serialize";
    private static final String ZK_SERIALIZER_SERIALIZE_CODE;

    private static final String ZK_SERIALIZER_DESERIALIZE_CODE_PATH = "/codes/zookeeperConfigurationHandler/zkSerializer/deserialize";
    private static final String ZK_SERIALIZER_DESERIALIZE_CODE;

    static{
        InputStream inputStream = ZookeeperConfigurationHandler.class.getClassLoader().getResourceAsStream("codes.xml");
        Document document = createDocument(inputStream);

        SET_ZK_SERIALIZER_CODE = findCode(document, SET_ZK_SERIALIZER_CODE_PATH);

        ZK_SERIALIZER_SERIALIZE_CODE = findCode(document, ZK_SERIALIZER_SERIALIZE_CODE_PATH);

        ZK_SERIALIZER_DESERIALIZE_CODE = findCode(document, ZK_SERIALIZER_DESERIALIZE_CODE_PATH);
    }

    public static String getClassName() {
        return CLASS_NAME.replaceAll("\\.", "/");
    }

    @Override
    public boolean isHandled() {
        return handled;
    }

    private CtClass addZkSerializerClass() throws NotFoundException, CannotCompileException, IOException{
        ClassPool classPool = ClassPool.getDefault();
        CtClass serializerClass = classPool.makeClass(ZK_SERIALIZER_CLASS_NAME);
        serializerClass.setInterfaces(new CtClass[]{classPool.get(ZK_SERIALIZER_INTERFACE_CLASS_NAME)});
        serializerClass.setModifiers(Modifier.PUBLIC);

        //serialize
        CtMethod serializeMethod = CtNewMethod.make(ZK_SERIALIZER_SERIALIZE_CODE, serializerClass);
        //deserialize
        CtMethod deserializeMethod = CtNewMethod.make(ZK_SERIALIZER_DESERIALIZE_CODE, serializerClass);

        serializerClass.addMethod(serializeMethod);
        serializerClass.addMethod(deserializeMethod);

        // 输出并加载class 类，默认加载到当前线程的ClassLoader中
        serializerClass.toClass();

        return serializerClass;
    }

    private CtClass setZkSerializer() throws NotFoundException, CannotCompileException, IOException{
        ClassPool classPool = ClassPool.getDefault();
        CtClass zookeeperConfigurationClass = classPool.get(CLASS_NAME);
        CtConstructor constructor = zookeeperConfigurationClass.getDeclaredConstructor(new CtClass[0]);
        constructor.insertAfter(SET_ZK_SERIALIZER_CODE);

        // 输出并加载class 类，默认加载到当前线程的ClassLoader中
//        zookeeperConfigurationClass.toClass();

        return zookeeperConfigurationClass;
    }

    @Override
    public byte[] handle() throws NotFoundException, CannotCompileException, IOException {
        handled = true;

        CtClass serializerClass = null;
        CtClass zookeeperConfigurationClass = null;
        try{
            // add ZkSerializer
            serializerClass = addZkSerializerClass();

            //set zkClient ZkSerializer
            zookeeperConfigurationClass = setZkSerializer();

            byte[] bytes = zookeeperConfigurationClass.toBytecode();

            return bytes;
        }finally {
            if(zookeeperConfigurationClass != null){
                //清除ClassPool缓存
                zookeeperConfigurationClass.detach();
            }
            if(serializerClass != null){
                //清除ClassPool缓存
                serializerClass.detach();
            }
        }
    }
}
