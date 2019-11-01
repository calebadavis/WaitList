package vegas.caleb.waitlist;

import android.os.Parcel;
import android.os.Parcelable;

public class Customer implements Parcelable {

    private String _fname, _lname, _phone;

    public String getFName() { return _fname; }
    public String getLName() { return _lname; }
    public String getPhone() { return _phone; }

    public Customer(String first, String last, String phone) {
        init(first, last, phone);
    }

    private void init(String first, String last, String phone) {
        _fname = first;
        _lname = last;
        _phone = phone;
    }

    private Customer(Parcel p) {
        init (
                p.readString(),
                p.readString(),
                p.readString()
        );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_fname);
        dest.writeString(_lname);
        dest.writeString(_phone);
    }

    public static final Parcelable.Creator<Customer> CREATOR = new Parcelable.Creator<Customer>() {
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };
}
