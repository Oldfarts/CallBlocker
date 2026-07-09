package com.example.callblocker;

import java.util.Set;


public final class NumberUtils {


    private NumberUtils() {
        // Estetään instanssin luonti
    }



    /**
     * Normalisoi puhelinnumeron vertailua varten.
     *
     * Esim:
     * +358 40 5744906
     * +358-40-5744906
     * 0405744906
     *
     */

    public static String normalize(String number) {


        if(number == null) {
            return "";
        }


        String n = number.trim();


        n = n.replace(
                "tel:",
                ""
        );


        n = n.replace(
                " ",
                ""
        );


        n = n.replace(
                "-",
                ""
        );


        n = n.replace(
                "(",
                ""
        );


        n = n.replace(
                ")",
                ""
        );



        /*
         * Muutetaan suomalainen
         *
         * 0401234567
         *
         * muotoon
         *
         * +358401234567
         *
         */

        if(n.startsWith("0")) {

            n =
                "+358"
                +
                n.substring(1);

        }


        return n;

    }





    /**
     * Tarkistaa löytyykö numero listasta.
     */

    public static boolean containsNumber(
            Set<String> numbers,
            String target
    ) {


        String normalizedTarget =
                normalize(target);



        for(String item : numbers) {


            if(normalize(item)
                    .equals(normalizedTarget)) {


                return true;

            }

        }


        return false;

    }





    /**
     * Onko suomalainen numero?
     */

    public static boolean isFinnishNumber(
            String number
    ) {


        String n =
                normalize(number);



        return n.startsWith(
                "+358"
        );

    }


}