package nl.amc.biolab.persistencemanager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.google.common.base.Joiner;

public class SQLBuilderPlugin {
    /**
     * Query variable for SQL builder functions
     */
    private String QUERY = "";
    
    /**
     * Sets select part of sql query
     * @param selections Map of selects we want to make, pass in like: Map<selection criteria, alias> (e.g.: Map<'p.data', 'dataName'>) 
     */
    public void setSelect(LinkedHashMap<String, String> selections) {
        _setQuery("SELECT");

        ArrayList<String> selectSql = new ArrayList<String>();

        for (Entry<?, ?> entry : selections.entrySet()) {
            if (entry.getValue() != null && entry.getValue().toString().length() != 0) {
                selectSql.add(entry.getKey().toString() + " as " + entry.getValue().toString());
            } else {
                selectSql.add(entry.getKey().toString());
            }
        }

        Joiner commaJoiner = Joiner.on(", ").skipNulls();

        _setQuery(commaJoiner.join(selectSql));
    }

    /**
     * Sets from part of sql query
     * @param tables Map of tables we want to select from, pass in like: Map<table name, alias> (e.g.: Map<'Project', 'p'>)
     */
    public void setTables(LinkedHashMap<String, String> tables) {
        _setQuery("FROM");

        ArrayList<String> tableSql = new ArrayList<String>();

        for (Entry<?, ?> entry : tables.entrySet()) {
            if (entry.getValue() != null && entry.getValue().toString().length() != 0) {
                tableSql.add(entry.getKey().toString() + " as " + entry.getValue().toString());
            } else {
                tableSql.add(entry.getKey().toString());
            }
        }

        Joiner commaJoiner = Joiner.on(", ").skipNulls();

        _setQuery(commaJoiner.join(tableSql));
    }

    /**
     * Sets join part of sql query
     * @param tables Map of tables we want to join, pass in like: Map<table name, on criteria> (e.g.: Map<'Processing as po', 'p.ProcessingID = po.ProcessingID'>)
     */
    public void setJoin(LinkedHashMap<String, String> tables) {
        ArrayList<String> joinSql = new ArrayList<String>();

        for (Entry<?, ?> entry : tables.entrySet()) {
            joinSql.add("JOIN " + entry.getKey().toString() + " ON " + entry.getValue().toString());
        }

        Joiner spaceJoiner = Joiner.on(" ").skipNulls();

        _setQuery(spaceJoiner.join(joinSql));
    }

    /**
     * Returns formatted where for input
     * @param name Database column name
     * @param modifier Sql modifier (e.g.: =, !=, LIKE)
     * @param where Search parameter
     * @return Formatted string for use in sql query
     */
    public String getWhere(String name, String modifier, String where) {
        return name + " " + modifier + " " + where;
    }

    /**
     * Combines two wheres into an or search
     * @param a getWhere one
     * @param b getWhere two
     * @return Formatted string for use in sql query
     */
    public String getWhereOr(String a, String b) {
    	return a + " OR " + b;
    }
    
    /**
     * Sets where part of sql query
     * @param where Map of where we want
     */
    public void setWhere(LinkedHashMap<Object, String> where) {
    	_setQuery("WHERE");

		_setQuery(_setWhere(where, true));
    }
    
    /**
     * Get formatted sql where string from input
     * @param where LinkedHashMap of where items we want formatted
     * @param first Boolean whether if this is the first call to the function or not
     * @return Formatted string of sql where
     */
	@SuppressWarnings("unchecked")
	private String _setWhere(LinkedHashMap<Object, String> where, boolean first) {
    	String whereSql = "";
    	
        for (Entry<Object, String> entry : where.entrySet()) {
        	if (!first) {
    			whereSql += " " + entry.getValue() + " ";
    		}
        	
        	if (entry.getKey() instanceof LinkedHashMap<?, ?>) {
        		whereSql += "(" + _setWhere((LinkedHashMap<Object, String>) entry.getKey(), true) + ")";
        	} else if (entry.getKey() instanceof ArrayList<?>) {
        		Joiner commaJoiner = Joiner.on(" " + entry.getValue() + " ").skipNulls();
        		
        		whereSql += "(" + commaJoiner.join((ArrayList<String>) entry.getKey()) + ")";
        	} else {
        		whereSql += entry.getKey();
        	}
        	
        	first = false;
        }
        
        return whereSql;
    }

	/**
	 * Set the sql order part of the query
	 * @param order LinkedHashMap of orders 
	 */
    public void setSort(LinkedHashMap<String, String> order) {
        _setQuery("ORDER BY");

        ArrayList<String> orderSql = new ArrayList<String>();

        for (Entry<?, ?> entry : order.entrySet()) {
            orderSql.add(entry.getKey() + " " + entry.getValue());
        }

        Joiner commaJoiner = Joiner.on(", ").skipNulls();

        _setQuery(commaJoiner.join(orderSql));
    }
    
    /**
     * Set the sql limit part of the query
     * @param limit String of limit
     */
    public void setLimit(String limit) {
    	_setQuery("LIMIT");
    	_setQuery(limit);
    }

    /**
     * Add input to query class variable
     * @param query Input to add to query class variable
     */
    private void _setQuery(String query) {
        QUERY = QUERY + query + " ";
    }
    
    /**
     * Reset the query class variable to an empty string
     */
    public void resetQuery() {
    	QUERY = "";
    }

    /**
     * Get the query class variable
     * @return Query class variable as string
     */
    public String getQuery() {
        return QUERY;
    }
}
