package com.mygdx.java.server.handler;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mygdx.java.common.data.Message;
import com.mygdx.java.utils.ImageUtils;

public class ForServerIoHandler implements IoHandler {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ForServerIoHandler.class);

	private final Set<IoSession> sessions = Collections
			.synchronizedSet(new HashSet<IoSession>());

	@Override
	public void exceptionCaught(IoSession iosession, Throwable throwable)
			throws Exception {

		LOGGER.error("exceptionCaught | " + getAddress(iosession) + " | "
				+ iosession.getId() + " | " + throwable.getMessage());
		iosession.close(true);
	}

	@Override
	public void messageReceived(IoSession iosession, Object obj)
			throws Exception {
		LOGGER.debug("messageReceived | " + getAddress(iosession) + " | "
				+ iosession.getId() + " | " + obj);

		LOGGER.info(obj + " ---> " + (obj instanceof Message));
		if (obj instanceof Message) {
			LOGGER.info("messageReceived | " + getAddress(iosession) + " | "
					+ iosession.getId() + " | Message | " + obj);
			Message message = (Message) obj;
			if (message.getType() == Message.CLIENT_GET_DATA) {
				System.out.println("---->" + message);
				Message data = new Message(message.getId(),
						Message.SERVER_SEND_DATA,
						ImageUtils.getScreenBufferedImageBytes());
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode jsonNode = objectMapper.readTree(data.toString());
				System.out.println(data.toString() + " sha1 = "
						+ jsonNode.path("sha1"));
				iosession.write(data);
				return;
			}

			if (message.getType() == Message.QUIT) {
				iosession.write(new Message(iosession.getId(), Message.QUIT,
						"ClientQuitOK".getBytes()));
				LOGGER.info("ClientQuit");
				iosession.close(true);
				return;
			}
		}

	}

	@Override
	public void messageSent(IoSession iosession, Object obj) throws Exception {
		LOGGER.debug("messageSent | " + getAddress(iosession) + " | "
				+ iosession.getId() + " | " + obj);
		if (obj instanceof Message) {
			LOGGER.info("messageSent | " + iosession.getId() + " | Message | "
					+ obj);
		}
	}

	@Override
	public void sessionClosed(IoSession iosession) throws Exception {
		LOGGER.debug("sessionClosed | " + getAddress(iosession) + " | "
				+ iosession.getId() + " | ");
		iosession.close(true);
	}

	@Override
	public void sessionCreated(IoSession iosession) throws Exception {
		LOGGER.debug("sessionCreated | " + getAddress(iosession) + " | "
				+ iosession.getId() + " | ");

	}

	@Override
	public void sessionIdle(IoSession iosession, IdleStatus idlestatus)
			throws Exception {
		LOGGER.debug("sessionIdle | " + getAddress(iosession) + " | "
				+ iosession.getId() + " | " + idlestatus);
		iosession.close(true);
	}

	@Override
	public void sessionOpened(IoSession iosession) throws Exception {
		LOGGER.debug("sessionOpened | " + getAddress(iosession) + " | "
				+ iosession.getId() + " | ");

	}

	private String getAddress(IoSession iosession) {
		InetSocketAddress inetSocketAddress = (InetSocketAddress) iosession
				.getRemoteAddress();
		return inetSocketAddress.getAddress().toString();
	}

	@Override
	public void inputClosed(IoSession iosession) throws Exception {
		LOGGER.debug("inputClosed | " + getAddress(iosession) + " | "
				+ iosession.getId() + " | ");
		iosession.close(true);
	}
}
