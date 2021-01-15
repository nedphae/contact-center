package com.qingzhu.staffadmin.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableR2dbcRepositories("com.qingzhu.staffadmin.*.repository")
@EnableR2dbcAuditing
@EnableTransactionManagement
@EnableJpaRepositories
class DatabaseConfiguration