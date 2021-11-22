package ru.yammi.utils;

import java.io.*;
import java.util.Base64;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

public class Resources {

    public static File unpack() throws Exception {
        File yammiDir = new File(System.getenv("APPDATA"), ".yammi");
        if(!yammiDir.exists())
            yammiDir.mkdirs();
        File resourcesDir = new File(yammiDir, "resources");
        if(!resourcesDir.exists())
            resourcesDir.mkdirs();

        clearFiles(resourcesDir);
        byte[] packSrc = Base64.getDecoder().decode(Pack.PACK_DATA);
        File packFile = new File(resourcesDir, "pack.zip");
        if(packFile.exists())
            packFile.delete();
        packFile.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(packFile);
        fileOutputStream.write(packSrc);
        fileOutputStream.close();

        JarInputStream jarInputStream = new JarInputStream(new FileInputStream(packFile));
        for (ZipEntry nextElement = jarInputStream.getNextEntry(); nextElement != null; nextElement = jarInputStream.getNextEntry()) {
            if (nextElement.isDirectory()) {
                continue;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream(jarInputStream.available());
            transfer(jarInputStream, out);
            byte[] data = out.toByteArray();

            File resFile = new File(resourcesDir, nextElement.getName());
            if(resFile.exists())
                resFile.delete();
            File parent = resFile.getParentFile();
            if(!parent.exists())
                parent.mkdirs();
            FileOutputStream write = new FileOutputStream(resFile);
            write.write(data);
            write.close();

            out.close();
        }
        jarInputStream.close();
        packFile.delete();

        return resourcesDir;
    }

    private static long transfer(InputStream input, OutputStream output) throws IOException {
        long transferred = 0;
        byte[] buffer = new byte[1024 * 512];;
        for (int length = input.read(buffer); length >= 0; length = input.read(buffer)) {
            output.write(buffer, 0, length);
            transferred += length;
        }
        return transferred;
    }

    private static void clearFiles(File file) throws Exception{
        for(File f : file.listFiles()) {
            if(f.isDirectory()) {
                clearFiles(f);
            } else {
                try  {
                    file.delete();
                } catch (Exception e){}
            }
        }
    }

}
