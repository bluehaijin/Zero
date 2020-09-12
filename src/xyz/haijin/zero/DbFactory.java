package xyz.haijin.zero;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import xyz.haijin.zero.util.DbUtil;


public class DbFactory {
	
	private static String[][] configs;

	
	private void init() {
		if(configs == null) {
				Set<String> set = DbUtil.getNameSet();
				if(set != null && set.size() != 0) {
					configs = new String[set.size()][5]; 
					int i = 0;
					for(String key : set) {
						Map<String,String> contexts = DbUtil.getContext(key);
						String name = contexts.get("name");
						String url = contexts.get("url");
						String username = contexts.get("username");
						String password = contexts.get("password");
						String driver = contexts.get("driver");
						configs[i][0] = name;
						configs[i][1] = url;
						configs[i][2] = username;
						configs[i][3] = password;
						configs[i][4] = driver;
						i++;
					}
				}
		}
	}
	private static DbFactory instance;

	private HashMap<String, DbConnection> dbConnections = new HashMap<String, DbConnection>();
	
	private DbFactory() {
		init();
		for(int i = 0; i < configs.length; i++) {
			String config[] = configs[i];
			DbConnection connection = new DbConnection(config[1], config[2], config[3],config[4]);
			dbConnections.put(config[0], connection);
		}
	}
    
    public static DbConnection getInstance(String key) {
    	if(instance == null) {
    		instance = new DbFactory();
    	}
    	return instance.dbConnections.get(key);
    }
    
    public static void closeStatement(Statement statement) {
    	try {
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    public static void main(String args[]) throws SQLException {
    }
    
}
