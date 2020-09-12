package xyz.haijin.zero;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseDAO<T> {
	protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	protected static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	protected String findByIdSql = null;
	protected String findAllSql = null;
	protected String findWhereSql = null;
	protected String findNoWhereSql = null;
	protected String findCountSql = null;
	private String dbName = null;
	private String tableName = null;
	private String tableColumn = "";
	private String tableWhere = "";
	private String tableNoWhere = "";
	private String id1 = null;
	private String id2 = null;
	private Class<T> beanClass = null;
	private DbConnection connection = null;
	private Map<String, Field> fieldMap = new HashMap<String, Field>();
	private List<String> fieldList = new ArrayList<String>();

	protected BaseDAO(Class<T> clazz, String db) {
		dbName = db;
		connection = DbFactory.getInstance(db);
		beanClass = clazz;
		tableName = clazz.getAnnotation(Table.class).value();
		if (clazz.getAnnotation(Where.class) != null) {
			tableWhere = clazz.getAnnotation(Where.class).value();
		}
		
		if (clazz.getAnnotation(NoWhere.class) != null) {
			tableNoWhere = clazz.getAnnotation(NoWhere.class).value();
		}
		
		System.out.println("tableName:"+tableName);
		for(Field field : clazz.getDeclaredFields()) {
			if ("".equals(tableColumn)) {
				if (field.getAnnotation(ID.class) != null) {
					tableColumn += field.getAnnotation(ID.class).value();
				} else {
					if (field.getAnnotation(Column.class) != null) {
						tableColumn += field.getAnnotation(Column.class).value();
					} else {
						continue;
					}
				}
			} else {
				tableColumn += ","+field.getAnnotation(Column.class).value();
			}
		}
		System.out.println("tableColumn:"+tableColumn);
		initFieldMap();
		findByIdSql = "select "+tableColumn+" from " + tableName + " where `" + id2 + "`=%s";
		findAllSql = "select "+tableColumn+" from " + tableName;
	}
	
	private void generateNoWhereSql() throws Exception {
		if (!"".equals(tableNoWhere)) {
			findNoWhereSql ="select "+tableColumn+" from " + tableName + " "+tableNoWhere;
		} else {
			throw new Exception("请添加@NoWhere注解");
		}
	}

	private void generateCountSql() throws Exception {
		findCountSql = "select count(*) from " + tableName;
		if (!"".equals(tableWhere)) {
			findCountSql = findCountSql + " where "+tableWhere;
		} else {
			throw new Exception("请添加@Where注解");
		}
		if (!"".equals(tableNoWhere)) {
			findCountSql = findCountSql +" " + tableNoWhere;
		}
	}
	
	private void generateWhereSql() throws Exception {
		if (!"".equals(tableWhere)) {
			findWhereSql = "select "+tableColumn+" from " + tableName + " where "+tableWhere;
		} else {
			throw new Exception("请添加@Where注解");
		}
		if (!"".equals(tableNoWhere)) {
			findWhereSql = findWhereSql +" " + tableNoWhere;
		}
	}

	/**
	 * 没有@NoWhere注解即没有where语句的方式查询
	 * @return
	 * @throws SQLException
	 */
	public int findCountSql(Object ...strs) throws SQLException {
		int count = 0;
		try {
			generateCountSql();
		} catch (Exception e) {
			e.printStackTrace();
		}
		PreparedStatement pstat = connection.getStatement(findCountSql);
		if (strs != null && strs.length > 0) {
			for (int i = 1 ; i <= strs.length ; i++) {
				pstat.setObject(i, strs[i-1]);
				System.out.println("第"+i+"个参数值："+strs[i-1]);
			}
		}
		ResultSet resultSet = pstat.executeQuery();
		if(resultSet != null) {
			try {
				while(resultSet.next()){
					count = resultSet.getInt(1);
				}
				resultSet.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return count;
	}
	
	/**
	 * 没有@NoWhere注解即没有where语句的方式查询
	 * @param values
	 * @return
	 * @throws SQLException
	 */
	public List<T> findNoWhereSql(Object ...values) throws SQLException {
		try {
			generateNoWhereSql();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Object[] Strs = null;
		if (values != null && values.length > 0) {
			Strs = values;
		}
		return findPreparedBySql(Strs,findNoWhereSql);
	}
	
	/**
	 * 通过有@Where注解即有where语句的方式查询记录（可包含没有@NoWhere注解即没有where语句的方式查询）
	 * @param values
	 * @return
	 * @throws SQLException
	 */
	public List<T> findWhereSql(Object ...values) throws SQLException {
		try {
			generateWhereSql();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Object[] Strs = null;
		if (values != null && values.length > 0) {
			Strs = values;
		}
		return findPreparedBySql(Strs,findWhereSql);
	}
	
	private String getIdKey1() {
		return id1;
	}
	
	private String getIdKey2() {
		return id2;
	}

	private String getDbName() {
		return dbName;
	}

	private String getTableName() {
		return tableName;
	}
	
	private void initFieldMap() {
		Field[] fields = beanClass.getDeclaredFields();
		for(int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String propertyName = null;
			if(field.isAnnotationPresent(Column.class)) {
				propertyName = field.getAnnotation(Column.class).value();
			} else if(field.isAnnotationPresent(ID.class)) {
				propertyName = field.getAnnotation(ID.class).value();
				if(id1==null || "".equals(id1)) {
					id1 = propertyName;
					id2 = propertyName;
				}else {
					id2 = propertyName;
				}
			}
			if(propertyName != null) {
				fieldMap.put(propertyName, field);
				fieldList.add(propertyName);
			}
			field.setAccessible(true);
		}
		System.out.println("字段个数:"+fields.length);
	}

	/**
	 * 查询所有记录
	 * @return
	 */
	public List<T> findAll() {
		return findBySql(findAllSql);
	}
	
	/**
	 * 通过实体类更新一条记录并返回新增记录的ID
	 * @param instance
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public Object saveAutoID(T instance) throws IllegalArgumentException, IllegalAccessException {
		StringBuffer buffer = new StringBuffer();
		buffer.append("insert into `" + getTableName() + "`(");
		int count = 0;
		String valueStr = "";
		for(int i = 0; i < fieldList.size(); i++) {
			String key = fieldList.get(i);
			Field field = fieldMap.get(key);
			Class<?> parameterType = field.getType();
			Object value = field.get(instance);
			if(value != null) {
				if(String.class.equals(parameterType)) {
					value = "'" + value + "'";
				} else if(Date.class.equals(parameterType)) {
					value = "'" + DATETIME_FORMAT.format((Date)value) + "'";
				}
				
				if(count > 0) {
					buffer.append(",");
					valueStr += ",";
				}
				buffer.append("`"+key+"`");
				valueStr += value;
				count++;
			}
		}
		buffer.append(") values(");
		buffer.append(valueStr);
		buffer.append(")");
		String sql = "SELECT LAST_INSERT_ID()";
		Object id = null;
		ResultSet rs = null;
		try {
			System.out.println(buffer.toString());
			connection.getStatement().execute(buffer.toString());
			rs = connection.returnStatement().executeQuery(sql);
			if(rs.next()) {
				id = rs.getObject(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				rs.close();
				connection.getStatement().close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return id;
	}

	/**
	 * 通过主键更新一条记录
	 * @param value
	 * @return
	 */
	public T findById(Object value) {
		if(value instanceof CharSequence) {
			value = "'" + value + "'";
		}
		String sql = String.format(findByIdSql, value);
		ResultSet resultSet = connection.query(sql);
		T object = null;
        try {
			if(resultSet.next()){
				object = generateObject(resultSet);
			}
	        resultSet.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}

	
	private List<T> findPreparedBySql(Object[] strs,String sql) throws SQLException {
		PreparedStatement pstat = connection.getStatement(sql);
		if (strs != null && strs.length > 0) {
			for (int i = 1 ; i <= strs.length ; i++) {
				pstat.setObject(i, strs[i-1]);
				System.out.println("第"+i+"个参数值："+strs[i-1]);
			}
		}
		
		ResultSet resultSet = pstat.executeQuery();
		List<T> list = new ArrayList<T>();
		if(resultSet != null) {
	        try {
				while(resultSet.next()){
					T object = generateObject(resultSet);
				    list.add(object);
				}
		        resultSet.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	private List<T> findBySql(String sql) {
		ResultSet resultSet = connection.query(sql);
		List<T> list = new ArrayList<T>();
		if(resultSet != null) {
	        try {
				while(resultSet.next()){
					T object = generateObject(resultSet);
				    list.add(object);
				}
		        resultSet.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * 通过一个实体类保存一条记录
	 * @param instance
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void save(T instance) throws IllegalArgumentException, IllegalAccessException {
		StringBuffer buffer = new StringBuffer();
		buffer.append("insert into `" + getTableName() + "`(");
		int count = 0;
		String valueStr = "";
		for(int i = 0; i < fieldList.size(); i++) {
			String key = fieldList.get(i);
			Field field = fieldMap.get(key);
			Class<?> parameterType = field.getType();
			Object value = field.get(instance);
			if(value != null) {
				if(String.class.equals(parameterType)) {
					value = "'" + value + "'";
				} else if(Date.class.equals(parameterType)) {
					value = "'" + DATETIME_FORMAT.format((Date)value) + "'";
				}
				
				if(count > 0) {
					buffer.append(",");
					valueStr += ",";
				}
				buffer.append("`"+key+"`");
				valueStr += value;
				count++;
			}
		}
		buffer.append(") values(");
		buffer.append(valueStr);
		buffer.append(")");
		
		connection.execute(buffer.toString());
	}

	/**
	 * 通过主键的值更新一条记录
	 * @param instance
	 * @param id
	 * @throws Exception
	 */
	public void update(T instance, Object id) throws Exception {
		String sql = "update `" + getTableName() + "` set " + generateUpdateSqlParams(instance) + " where `" + getIdKey1() + "`=" + "'"+id+"'";
		connection.execute(sql);
	}
	
	/**
	 * 通过两个主键的值更新一条记录
	 * @param instance
	 * @param id1
	 * @param id2
	 * @throws Exception
	 */
	public void update(T instance, String id1,String id2) throws Exception {
		String sql = "update `" + getTableName() + "` set " + generateUpdateSqlParams(instance) + " where `" + getIdKey1() + "`=" + "'"+id1+"'"
				+" and `" + getIdKey2() + "`=" + "'"+id2+"'";
		connection.execute(sql);
	}
	
	/**
	 * 通过字段值更新一条记录
	 * @param instance
	 * @param attribute
	 * @param value
	 * @throws Exception
	 */
	public void updateByAttribute(T instance, String attribute,Object value) throws Exception {
		String sql = "update `" + getTableName() + "` set " + generateUpdateSqlParams(instance) + " where `" + attribute + "`=" + value;
		connection.execute(sql);
	}
	
	/**
	 * 通过两个主键删除一条记录
	 * @param id1
	 * @param id2
	 * @throws Exception
	 */
	public void delete(String id1,String id2) throws Exception {
		String sql = "delete from `" + getTableName() +  "` where `" + getIdKey1() + "`=" + "'"+id1+"'"
				+" and `" + getIdKey2() + "`=" + "'"+id2+"'";
		connection.execute(sql);
	}
	
	/**
	 * 通过一个主键删除一条记录
	 * @param id1
	 * @throws Exception
	 */
	public void delete(String id1) throws Exception {
		String sql = "delete from `" + getTableName() +  "` where `" + getIdKey1() + "`=" + "'"+id1+"'";
		connection.execute(sql);
	}
	
	/**
	 * 通过字段的值删除一条记录
	 * @param attribute
	 * @param value
	 * @throws Exception
	 */
	public void deleteByAttribute(String attribute,Object value) throws Exception {
		String sql = "delete from `" + getTableName() +  "` where `" + attribute+ "`=" +value;
		connection.execute(sql);
	}
	
	/**
	 * 通过字段的多个值删除多条记录
	 * @param attribute
	 * @param value
	 * @throws Exception
	 */
	public void deleteByAttributes(String attribute,Object[] value) throws Exception {
		StringBuffer values = new StringBuffer();
		for(int i = 0 ; i < value.length ; i++) {
			if(i == 0) {
				values.append(value[i]);
			}else {
				values.append(","+value[i]);
			}
		}
		String sql = "delete from `" + getTableName() +  "` where `" + attribute+ "`in(" +values+")";
		connection.execute(sql);
	}

	private T generateObject(ResultSet rs) throws Exception {
		T object = (T) beanClass.newInstance();
		for(int i = 0; i < fieldList.size(); i++) {
			String key = fieldList.get(i);
			Field filed = fieldMap.get(key);
			Class<?> parameterType = filed.getType();
			if(String.class.equals(parameterType)) {
				filed.set(object, rs.getString(key));
			} else if(boolean.class.equals(parameterType) || Boolean.class.equals(parameterType)) {
				filed.set(object, rs.getBoolean(key));
			} else if(int.class.equals(parameterType) || Integer.class.equals(parameterType)) {
				filed.set(object, rs.getInt(key));
			} else if(float.class.equals(parameterType) || Float.class.equals(parameterType)) {
				filed.set(object, rs.getFloat(key));
			} else if(Date.class.equals(parameterType)) {
				filed.set(object, rs.getTimestamp(key));
			}
		}
		return object;
	}
	
	private boolean isEmpty(String str) {
		return str == null && "".equals(str);
	}
	
	private String generateUpdateSqlParams(T object) throws Exception {
		StringBuffer buffer = new StringBuffer();
		int count = 0;
		for(int i = 0; i < fieldList.size(); i++) {
			String key = fieldList.get(i);
			Field field = fieldMap.get(key);
			Class<?> parameterType = field.getType();
			Object value = field.get(object);
			if(value != null) {
				if(String.class.equals(parameterType)) {
					if(isEmpty(""+value)) {
						continue;//濡傛灉璇ュ瓧娈电殑鍊间负绌哄垯涓嶈繘琛岃窡鏂�?
					}
					value = "'" + value + "'";
				} else if(Date.class.equals(parameterType)) {
					value = "'" + DATETIME_FORMAT.format((Date)value) + "'";
				}
				if(count > 0) {
					buffer.append(",`");
				} else {
					buffer.append("`");
				}
				buffer.append(key + "`=" + value);
				count++;
			}
		}
		return buffer.toString();
	}
	
	private String toFileData(T obj) {
		StringBuffer buffer = new StringBuffer();
		try {
			for(int i = 0; i < fieldList.size(); i++) {
				String key = fieldList.get(i);
				Field field = fieldMap.get(key);
				if(i > 0) {
					buffer.append("\t");
				}
				Object value = field.get(obj);
				if(value != null) {
					if(value instanceof Boolean) {
						char chr = 1;
						buffer.append((Boolean)value?chr:"\0");
					} else if(value instanceof Date) {
						buffer.append(DATETIME_FORMAT.format((Date)value));
					} else if(value instanceof String) {
						buffer.append(((String) value).replace("\\", "\\\\").replace("\t", "").replace("\r", ""));
					} else {
						buffer.append(value);
					}
				} else {
					buffer.append("\\N");
				}
			}
		} catch (Exception e) {
			buffer.delete(0, buffer.length());
			e.printStackTrace();
		}
		return buffer.toString();
	}
	
	private String getFileDataColumns() {
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i < fieldList.size(); i++) {
			String key = fieldList.get(i);
			if(i > 0) {
				buffer.append(",");
			}
			buffer.append(key);
		}
		return buffer.toString();
	}
	
	private List<String> getColumns() {
		return fieldList;
	}
}