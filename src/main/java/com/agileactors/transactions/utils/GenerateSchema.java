package com.agileactors.transactions.utils;

import com.agileactors.transactions.entity.AccountEntity;
import com.agileactors.transactions.entity.TransactionEntity;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;

import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper for validating the hibernate entities written.
 */
public class GenerateSchema {

    /**
     * Generates database create commands for the specified entities using Hibernate native API, SchemaExport.
     * Creation commands are exported into the create.sql file.
     */
    public static void main(String... args) {
        Map<String, Object> settings = new HashMap<>();
        settings.put(Environment.URL, "jdbc:h2:mem:schema");
        settings.put(Environment.DIALECT, "org.hibernate.dialect.SQLServer2012Dialect");
        settings.put(Environment.DRIVER, "org.h2.Driver");
        settings.put(Environment.USER, "sa");
        settings.put(Environment.PASS, "");
        settings.put(Environment.HBM2DDL_AUTO, "create-drop");
        settings.put(Environment.USE_SECOND_LEVEL_CACHE, "false");
        settings.put(Environment.USE_QUERY_CACHE, "false");

        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(settings).build();
        var metadataSources = new MetadataSources(serviceRegistry);
        metadataSources.addAnnotatedClass(AccountEntity.class);
        metadataSources.addAnnotatedClass(TransactionEntity.class);
        var metadata = metadataSources.buildMetadata();

        var root = FileSystems.getDefault().getPath("").toAbsolutePath();
        var filePath = Paths.get(root.toString(), "target", "sql", "transactions.model.sql");

        var schemaExport = new SchemaExport();
        schemaExport.setFormat(true);
        schemaExport.setOutputFile(filePath.toString());
        schemaExport.setDelimiter(";");
        schemaExport.createOnly(EnumSet.of(TargetType.SCRIPT), metadata);
    }
}
