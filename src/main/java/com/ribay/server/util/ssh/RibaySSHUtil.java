package com.ribay.server.util.ssh;

import com.jcraft.jsch.*;
import com.ribay.server.util.RibayProperties;
import com.sun.corba.se.spi.orbutil.fsm.Input;
import com.sun.mail.iap.ByteArray;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.*;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author Chris on 20.08.2016.
 */
public class RibaySSHUtil {

    private static final JSch sshConnector = new JSch();
    private static final String IDENTITY_NAME = "ribay_cluster";
    private final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(RibaySSHUtil.class);
    private static RibaySSHUtil INSTANCE = null;

    private RibayProperties properties;
    private List<String> hostsList;
    private Session session;
    private ChannelExec channel;
    private String user;
    private String host;
    private int port;

    private RibaySSHUtil() {
        try {
            properties = new RibayProperties();
            hostsList = new ArrayList<>(Arrays.asList(properties.getDatabaseIps()));
            // setup ssh connections
            user = properties.getSSHUser();
            host = hostsList.get(0);
            port = Integer.valueOf(properties.getSSHPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RibaySSHUtil getInstance() {
        if(null == INSTANCE) {
            INSTANCE = new RibaySSHUtil();
        }
        return INSTANCE;
    }

    public void startConnection() {
        try {
            setupIdentity();
            establishSession();
            openChannel();
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void setupIdentity() throws IOException, JSchException, URISyntaxException {
        if(sshConnector.getIdentityNames().isEmpty()) {
            ClassLoader classLoader = RibaySSHUtil.class.getClassLoader();
            URL url = classLoader.getResource(properties.getSSHPrivateKey());
            Path path = Paths.get(url.toURI());
            byte[] privateKey = Files.readAllBytes(path);
            url = classLoader.getResource(properties.getSSHPublicKey());
            path = Paths.get(url.toURI());
            byte[] publicKey = Files.readAllBytes(path);
            byte[] passphrase = properties.getSSHPassphrase().getBytes();
            sshConnector.addIdentity(IDENTITY_NAME, privateKey, publicKey, passphrase);
        }
    }

    private void establishSession() throws JSchException {
        if(null == session || !session.isConnected()) {
            session = sshConnector.getSession(user, host, port);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            session.setConfig(sshConfig);
            session.connect(30000);
        }
    }

    private void openChannel() {
        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setOutputStream(System.out);
            channel.setInputStream(System.in);
            channel.setErrStream(System.err);
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    public String executeSSHCommand (String s) {
        LOGGER.debug("Sending SSH command: " + s);
        String result = "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // channel preparation (create instance, set command, connect)
            if (null == channel) {
                channel = (ChannelExec) session.openChannel("exec");
            }
            channel.setOutputStream(baos);
            channel.setInputStream(null);
            channel.setCommand(s);
            channel.connect();

            // read the output of the command
            result = readChannelOutput(channel.getInputStream());

            // disconnect the channel
            channel.disconnect();
            channel = null;
            LOGGER.debug("Done executing SSH command");
        } catch (Exception e) {
            LOGGER.error("Error occured while executing SSH command");
            e.printStackTrace();
        }

        return result.replaceAll("\n", "\r\n");
    }

    private String readChannelOutput(InputStream in){
        StringBuilder result = new StringBuilder("");
        try {

            byte[] tmp=new byte[1024];
            while(true){
                while(in.available()>0){
                    int i=in.read(tmp, 0, 1024);
                    if(i<0)break;
                    result.append(new String(tmp, 0, i));
                }
                if(channel.isClosed()){
                    if(in.available()>0) continue;
                    LOGGER.debug("SSH Command Exit Status: " + channel.getExitStatus());
                    break;
                }
                try{Thread.sleep(500);}catch(Exception ee){break;}
            }
        } catch(Exception e) {
            LOGGER.error("Error occurred while receiving SSH output");
            e.printStackTrace();
        }

        return result.toString();
    }
}
