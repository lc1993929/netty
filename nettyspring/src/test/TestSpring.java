package test;

import org.apache.log4j.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSpring {

	private static Logger log = Logger.getLogger(TestSpring.class);

	public static void main(String[] args) {
		// 加载spirng配置文件
		ApplicationContext context = new ClassPathXmlApplicationContext("server.xml");
	}

}