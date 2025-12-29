package listeners;

import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;

// Listens to RowSet data changes - implements RowSetListener interface
public class TableDataListener implements RowSetListener {

    private String listenerName;

    public TableDataListener(String listenerName) {
        this.listenerName = listenerName;
    }

    // Called when cursor position changes (navigation)
    @Override
    public void cursorMoved(RowSetEvent event) {
        System.out.println("[" + listenerName + "] Cursor moved in RowSet");

        // In UI implementation, this would update current row display
        // For now, just log the event
    }

    // Called when a row is changed (INSERT, UPDATE, DELETE)
    @Override
    public void rowChanged(RowSetEvent event) {
        System.out.println("[" + listenerName + "] Row changed in RowSet");

        // In UI implementation, this would refresh the table display
        // For now, just log the event
    }

    // Called when entire RowSet is changed (re-query, populate)
    @Override
    public void rowSetChanged(RowSetEvent event) {
        System.out.println("[" + listenerName + "] RowSet data changed completely");

        // In UI implementation, this would rebuild the entire table
        // For now, just log the event
    }

    // Get listener name
    public String getListenerName() {
        return listenerName;
    }
}
