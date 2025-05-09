package com.lumengaming.skillsaw.utility;

public class Tuple<L, R> {

    public final L L;
    public final R R;

    public Tuple(L x, R y) {
        this.L = x;
        this.R = y;
    }

    @Override
    public String toString() {
        return "(" + L + "," + R + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Tuple)) {
            return false;
        }

        Tuple<L, R> other_ = (Tuple<L, R>) other;

        // this may cause NPE if nulls are valid values for x or y. The logic may be improved to handle nulls properly, if needed.
        return other_.L.equals(this.L) && other_.R.equals(this.R);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((L == null) ? 0 : L.hashCode());
        result = prime * result + ((R == null) ? 0 : R.hashCode());
        return result;
    }
}
