package github.com.rev;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArtistList {
    private List<Artist> artists;

    public List<Artist> getArtist() {
        if (artists == null) {
            artists = new ArrayList<>();
        }
        return this.artists;
    }

    /**
     * Equals and hashcode, implemented here for our tests demonstrating the issue.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArtistList that = (ArtistList) o;
        return Objects.equals(artists, that.artists);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(artists);
    }
}
