package edu.ualberta.med.biobank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.EnversSchemaGenerator;
import org.hibernate.tool.hbm2ddl.SchemaExport;

@SuppressWarnings("nls")
public class BiobankSchemaExport {

	private final Connection dbCon;

	public static void main(String[] args) {
		try {
			new BiobankSchemaExport();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public BiobankSchemaExport() throws SQLException {
		Configuration config = new Configuration().configure();

		dbCon = DriverManager.getConnection(
				config.getProperty("hibernate.connection.url"),
				config.getProperty("hibernate.connection.username"),
				config.getProperty("hibernate.connection.password"));

		String[] split = config.getProperty("hibernate.connection.url").split(
				"/");

		if (split.length != 4) {
			throw new IllegalStateException(
					"badly formtatted hibernate.connection.url in hibernate configuration file");
		}

		dropDatabase(split[3]);

		SchemaExport schemaExport = new EnversSchemaGenerator(config).export();

		schemaExport.setOutputFile("schema.sql");
		schemaExport.drop(false, true);
		schemaExport.create(false, true);

		System.out.println("done!");
	}

	/*
	 * Drops and recreates the named database.
	 */
	private void dropDatabase(String name) throws SQLException {
		// Statement stmt = dbCon.createStatement();
		// stmt.execute("DROP DATABASE " + name + ";");
		// stmt.execute("CREATE DATABASE " + name + ";");

	}

}
