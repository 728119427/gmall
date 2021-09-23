package com.atguigu.gmall.common.config;

import com.atguigu.gmall.common.utils.FastDfsClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import(FastDfsClient.class)
@PropertySource("classpath:fast_dfs.properties")
public class FastDfsConfig {
}
