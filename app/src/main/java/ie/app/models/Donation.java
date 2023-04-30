package ie.app.models;

public class Donation {
    public String _id;
    public int amount;
    public String paymenttype;
    public int upvotes;

    public static int id = 0;

    public Donation (int amount, String method, int upvotes) {
        this._id = "donation" + (++id);
        this.amount = amount;
        this.paymenttype = method;
        this.upvotes = upvotes;
    }

    public Donation () {
        this.amount = 0;
        this.paymenttype = "";
        this.upvotes = 0;
    }

    public String toString()
    {
        return _id + ", " + amount + ", " + paymenttype + ", " + upvotes;
    }
}
