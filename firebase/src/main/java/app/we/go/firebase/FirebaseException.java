package app.we.go.firebase;

import com.google.firebase.database.DatabaseError;

/**
 * Created by Aristides Papadopoulos (github:talosdev).
 */
public class FirebaseException extends Exception {


    private DatabaseError databaseError;

    public FirebaseException(DatabaseError databaseError) {
        super(buildMessage(databaseError));
    }



    private static String buildMessage(DatabaseError databaseError) {
        StringBuilder sb = new StringBuilder();
        sb.append(databaseError.getMessage());
        sb.append(" : ");
        sb.append(databaseError.getDetails());
        sb.append(" [code = ");
        sb.append(databaseError.getCode());
        sb.append(" ]");

        return sb.toString();
    }

    public DatabaseError getDatabaseError() {
        return databaseError;
    }

    public void setDatabaseError(DatabaseError databaseError) {
        this.databaseError = databaseError;
    }
}
