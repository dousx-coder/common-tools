package io.github.dousxcoder.tools.io;

import java.io.*;

/**
 * 解决反序列化版本不一致问题
 *
 * @author dousx
 */
public class CompatibleInputStream extends ObjectInputStream {
    public CompatibleInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected ObjectStreamClass readClassDescriptor() throws ClassNotFoundException, IOException {
        ObjectStreamClass resultClassDescriptor = null;
        Class localClass = null;

        try {
            resultClassDescriptor = super.readClassDescriptor();
            localClass = Class.forName(resultClassDescriptor.getName());
            ObjectStreamClass localClassDescriptor = ObjectStreamClass.lookup(localClass);
            if (localClassDescriptor != null) {
                long localSUID = localClassDescriptor.getSerialVersionUID();
                long streamSUID = resultClassDescriptor.getSerialVersionUID();
                if (streamSUID != localSUID) {
                    StringBuffer buffer = new StringBuffer("Overriding serialized class version mismatch: ");
                    buffer.append("local serialVersionUID = ").append(localSUID);
                    buffer.append(" stream serialVersionUID = ").append(streamSUID);
                    resultClassDescriptor = localClassDescriptor;
                }
            }

            return resultClassDescriptor;
        } catch (ClassNotFoundException | IOException var9) {
            throw var9;
        }
    }

    /**
     * 修改byte数组中对象的VersionId，返回对象
     *
     * @param oldBytes oldBytes
     * @return {@link Object}
     * @throws IOException            IO异常
     * @throws ClassNotFoundException class异常
     */
    public static Object byteToObject(byte[] oldBytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream tokenByteArrayInputStream = new ByteArrayInputStream(oldBytes);
        CompatibleInputStream tokenInputStream = new CompatibleInputStream(tokenByteArrayInputStream);
        return tokenInputStream.readObject();
    }
}
