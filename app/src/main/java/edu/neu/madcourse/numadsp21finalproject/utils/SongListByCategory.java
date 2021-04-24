package edu.neu.madcourse.numadsp21finalproject.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.songview.SongItem;

public class SongListByCategory {

    static HashMap<String, List<SongItem>> categorySongMap;

    public static List<SongItem> getSongListForCategory(String category) {
        return categorySongMap.get(category);
    }

    public static void createSongListByCategory() {

        categorySongMap = new HashMap<>();
        List<SongItem> rock = new ArrayList(){{
            add(new SongItem("Stairway To Heaven","8:08",
                    "1jS_JKkQ1Fs", "Led Zeppelin", "Rock"));
            add(new SongItem("Bohemian Rhapsody","5:53",
                    "_ih5L6S7Ejk", "Queen", "Rock"));
            add(new SongItem("Smoke On The Water","5:54",
                    "n_xW6NeBRyU", "Deep Purple", "Rock"));
            add(new SongItem("Comfortably Numb","6:38",
                    "Kn02KT5oonU", "Pink Floyd", "Rock"));
            add(new SongItem("Paint In Black","3:23",
                    "GRXk3VNPSpY", "The Rolling Stones", "Rock"));
            add(new SongItem("Pinball Wizard", "3:00" ,"5lbWwObdB2o",
                    "The Who", "Rock"));
        }};
        categorySongMap.put("Rock", rock);

        List<SongItem> pop = new ArrayList(){{
            add(new SongItem("Thriller","5:59",
                    "DkB2oj916Kc", "Michael Jackson", "Pop"));
            add(new SongItem(" Like a Prayer","5:41",
                    "hUgoIVt00HU", "Madonna", "Pop"));
            add(new SongItem("When Doves Cry","4:06",
                    "GGoqXb_He7g", "Prince", "Pop"));
            add(new SongItem("I Wanna Dance with Somebody","5:04",
                    "QMmc5ed_cgY", "Whitney Houston", "Pop"));
            add(new SongItem("Baby One More Time","3:41",
                    "-4HaTdtgvHA", "Britney Spears", "Pop"));
        }};
        categorySongMap.put("Pop", pop);

        List<SongItem> hipHop = new ArrayList(){{
            add(new SongItem("California Love","4:06",
                        "h5jVkRtZ1Ck", "2Pac ft. Dr. Dre", "Hip Hop"));
            add(new SongItem("Fight the Power","4:38",
                    "WlNBIzPZKBo", "Public Enemy", "Hip Hop"));
            add(new SongItem("Shook Ones (Part II)","4:30",
                    "iXt454W6K1M", "Mobb Deep", "Hip Hop"));
            add(new SongItem("Passin' me By","5:03",
                    "OGmPnBesq2k", "The Pharcyde", "Hip Hop"));
            add(new SongItem("Dear Mama","4:56",
                    "YU6UjfFsMgg", "Tupac Shakur", "Hip Hop"));
        }};
        categorySongMap.put("Hip Hop", hipHop);

        List<SongItem> blues = new ArrayList(){{
            add(new SongItem("Pine Top Boogie","3:26",
                    "pK4uoKgN9Wo", "Pine Top Smith", "Blues"));
            add(new SongItem("Dust My Broom","2:53",
                    "kXsF3uhIQEU", "Elmore James", "Blues"));
            add(new SongItem("Boogie Chillun","3:16",
                    "mXBgusooSww", "John Lee Hooker", "Blues"));
        }};
        categorySongMap.put("Blues", blues);

        List<SongItem> jazz = new ArrayList(){{
            add(new SongItem("Take Five","3:18",
                    "fJcos7j_yLY", "Dave Brubeck", "Jazz"));
            add(new SongItem("So What","4:04",
                    "32i2f36tvnw", "Miles Davis", "Jazz"));
            add(new SongItem("Take the A Train","2:31",
                    "A8wcJ_j4NXA", "Duke Ellington", "Jazz"));
            add(new SongItem("Round Midnight","3:38",
                    "4lVWd-Iiq4M", "Thelonious Monk", "Jazz"));
            add(new SongItem("My Favorite Things","3:50",
                    "hi5oDRnW340", "John Coltrane", "Jazz"));
        }};
        categorySongMap.put("Jazz", jazz);

        List<SongItem> reggae = new ArrayList(){{
            add(new SongItem("No Woman, No Cry","4:26",
                    "tmOPsWvDsuo", "Bob Marley & the Wailers", "Reggae"));
            add(new SongItem("Israelites","3:003",
                    "zddg2B59cqA", "Desmond Dekker & the Aces", "Reggae"));
            add(new SongItem(" Stir It Up","2:31",
                    "A8wcJ_j4NXA", "Bob Marley & the Wailers", "Reggae"));
            add(new SongItem(" Pressure Drop","3:01",
                    "vAM2-8kvN_s", "Toots & the Maytals", "Reggae"));
            add(new SongItem("The Harder They Come","3:22",
                    "KjAT-P2txT8", "Jimmy Cliff", "Reggae"));
        }};
        categorySongMap.put("Reggae", reggae);

        List<SongItem> folk = new ArrayList(){{
            add(new SongItem("This Land Is Your Land","3:01",
                    "tr9DhsWzlyo", "Woody Guthrie", "Folk"));
            add(new SongItem("Blowin' in the wind","2:50",
                    "gz-FYMh1mLo", "Bob Dylan", "Folk"));
            add(new SongItem("City of New Orleans","4:51",
                    "_ltUr0X8sXg", "Steve Goodman", "Folk"));
            add(new SongItem("If I Had A Hammer","2:10",
                    "x6Bp0npHMYM", "Pete Seeger", "Folk"));
            add(new SongItem("Suzanne","3:56",
                    "H96ymOo1zlc", "Leonard Cohen", "Folk"));

        }};
        categorySongMap.put("Folk", folk);

        List<SongItem> country = new ArrayList(){{
            add(new SongItem("I Walk the Line","2:46",
                    "XtKBtUcunek", "Johnny Cash", "Country"));
            add(new SongItem("Jackson","3:09",
                    "bfoYMMQIOhs", "Johnny Cash & June Carter", "Country"));
            add(new SongItem("Stand By Your Man","2:44",
                    "UvUz6EVJm4M", "Tammy Wynette", "Country"));
            add(new SongItem("The Dance","3:04",
                    "zlRlR7XW7c8", "Garth Brooks", "Country"));
            add(new SongItem("Jolene","4:08",
                    "V81_mrnRSpM", "Dolly Parton", "Country"));
        }};
        categorySongMap.put("Country", country);

        List<SongItem> classical = new ArrayList(){{
            add(new SongItem( "O Fortuna","2:49",
                    "LjiACevOQfw", "Carl Orff", "Classical"));
            add(new SongItem("I Blue Danube","3:13",
                    "9aY-rtJAY94", "Johann Strauss", "Classical"));
            add(new SongItem("Dance of the Sugar Plum Fairy","2:27",
                    "sdduPpnqre4", "Ray Conniff", "Classical"));
            add(new SongItem("The Four Seasons","9:24",
                    "7hZGZrOxBT4", "Vivaldi", "Classical"));
        }};
        categorySongMap.put("Classical", classical);

        List<SongItem> soul = new ArrayList(){{
            add(new SongItem("Hold On, I'm Comin'","2:39",
                    "49fp1z1owcQ", "Sam & Dave", "Soul"));
            add(new SongItem("Respect","2:39",
                    "0GApz9a8vEk", "Aretha Franklin", "Soul"));
            add(new SongItem("Knock On Wood","3:55",
                    "6Aiz8QC2dMI", "Eddie Floyd", "Soul"));
            add(new SongItem("Mustang Sally","3:41",
                    "XnAkiGp8aFE", "Wilson Pickett", "Soul"));
            add(new SongItem("Hard to Handle","3:10",
                    "OLKc05_VjD0", "Otis Redding", "Soul"));
        }};
        categorySongMap.put("Soul", soul);

        List<SongItem> rNB = new ArrayList(){{
            add(new SongItem("Real Love","4:41",
                    "tOg0BonpCO8", "Mary J. Blige", "R&B"));
            add(new SongItem("Waterfalls","4:17",
                    "6m2IjnfYjPQ", "TLC", "R&B"));
            add(new SongItem("Fantasy","4:23",
                    "0AqFNEahRuk", "Mariah Carey", "R&B"));
            add(new SongItem("Poison","4:31",
                    "j4LYtOyyLeE", "Bell Biv DeVoe", "R&B"));
            add(new SongItem("Survivor","4:12",
                    "3TVs2FlcRW4", "Destiny's Child", "R&B"));
        }};
        categorySongMap.put("R&B", rNB);

        List<SongItem> heavyMetal = new ArrayList(){{
            add(new SongItem("Rainbow In The Dark","4:08",
                    "UdyQM2PNC9M", "Ronnie James Dio", "Heavy Metal"));
            add(new SongItem("Master of Puppets","8:17",
                    "dkXt18Bm4wM", "Metallica", "Heavy Metal"));
            add(new SongItem("Holy Wars","6:39",
                    "J-aeXJUkXhc", "Megadeth", "Heavy Metal"));
            add(new SongItem("Painkiller","7:32",
                    "iIbAWMiIsKA", "Judas Priest", "Heavy Metal"));
            add(new SongItem("Raining Blood","3:39",
                    "S2meg8A3z4Q", "Slayer", "Heavy Metal"));
        }};
        categorySongMap.put("Heavy Metal", heavyMetal);
    }
}
