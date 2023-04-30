package ie.app.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ie.app.models.Donation;

public class FirebaseAPI {

    static FirebaseDatabase database;

    public static List<Donation> getAll(String call) {
        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(call);

        List<Donation> ret = new ArrayList<Donation>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Donation data = childSnapshot.getValue(Donation.class);
                    ret.add(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });

        return ret;
    }

    public static Donation get(String call, String donationId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(call).child(donationId);

        DataSnapshot dataSnapshot = null;
        try {
            dataSnapshot = Tasks.await(ref.get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (dataSnapshot == null || !dataSnapshot.exists()) {
            // Không tìm thấy đối tượng Donation với ID tương ứng
            return null;
        } else {
            // Lấy thông tin của đối tượng Donation
            Donation donation = dataSnapshot.getValue(Donation.class);
            return donation;
        }
    }

    public static String delete(String call, String donationId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(call).child(donationId);
        final String[] result = new String[1];
        ref.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    // Xóa đối tượng Donation thành công
                    result[0] = "Delete successful";
                    Log.d("TAG", "Donation deleted successfully");
                } else {
                    // Xóa đối tượng Donation thất bại
                    result[0] = "Delete failed";
                    Log.w("TAG", "Failed to delete donation", databaseError.toException());
                }
            }
        });
        return result[0];
    }

    public static String deleteAll(String call) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(call);
        final String[] result = new String[1];

        ref.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    // Xóa toàn bộ cơ sở dữ liệu thành công
                    result[0] = "Delete successful";
                    Log.d("TAG", "All data deleted successfully");
                } else {
                    result[0] = "Delete failed";
                    // Xóa toàn bộ cơ sở dữ liệu thất bại
                    Log.w("TAG", "Failed to delete all data", databaseError.toException());
                }
            }
        });

        return result[0];
    }
}
