package github.com.rev;

import java.util.Objects;

public class Artist {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    /**
     * Equals and hashcode, implemented here for our tests demonstrating the issue.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Artist artist = (Artist) o;
        return Objects.equals(name, artist.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
