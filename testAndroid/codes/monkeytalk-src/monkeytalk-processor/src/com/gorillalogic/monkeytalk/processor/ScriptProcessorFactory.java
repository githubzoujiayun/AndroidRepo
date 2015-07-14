package com.gorillalogic.monkeytalk.processor;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Map;

import com.gorillalogic.monkeytalk.agents.IAgent;

public class ScriptProcessorFactory {
	private static Map<String, Class<?>> processorClassMap = new Hashtable<String, Class<?>>();
	static {
		registerDefaultScriptProcessor(ScriptProcessor.class);
	}

	/**
	 * Instantiate a script processor with the given projectDir and agent.
	 * 
	 * @param rootDir
	 *            the project location
	 * @param agent
	 *            the agent to use for sending commands
	 */
	public static ScriptProcessor createScriptProcessor(File rootDir, IAgent agent) {
		return createDefaultScriptProcessor(rootDir, agent);
	}

	public static ScriptProcessor createDefaultScriptProcessor(File rootDir, IAgent agent) {
		return createScriptProcessor("default", rootDir, agent);
	}

	public static ScriptProcessor createScriptProcessor(String key, File rootDir, IAgent agent) {
		ScriptProcessor scriptProcessor = null;
		Class<?> processorClass = processorClassMap.get(key);
		if (processorClass != null) {
			try {
				Constructor<?> c = processorClass.getConstructor(File.class, IAgent.class);
				scriptProcessor = (ScriptProcessor) c.newInstance(rootDir, agent);
			} catch (Exception e) {
				e.printStackTrace();
				scriptProcessor = null;
			}
		}
		return scriptProcessor;
	}

	public static void registerDefaultScriptProcessor(Class<?> processorClass) {
		registerScriptProcessor(processorClass);
		processorClassMap.put("default", processorClass);
	}

	public static void registerScriptProcessor(Class<?> processorClass) {
		processorClassMap.put(processorClass.getName(), processorClass);
	}
}
