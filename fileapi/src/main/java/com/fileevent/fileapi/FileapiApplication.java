package com.fileevent.fileapi;

import com.fileevent.common.messaging.FileMessagingConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(FileMessagingConfiguration.class)
public class FileapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileapiApplication.class, args);
	}

}
