# seata-bug-fix-agent
seata-bug-fix-agent

使用javaagent解决了seata的一些bug，比如：config type是zk，因未设置序列化（zkClient.setZkSerializer()）导致获取不到配置

依赖javassist，请使用3.18或更高版本
<dependency>
                <groupId>org.javassist</groupId>
                <artifactId>javassist</artifactId>
                <version>3.26.0-GA</version>
            </dependency>
