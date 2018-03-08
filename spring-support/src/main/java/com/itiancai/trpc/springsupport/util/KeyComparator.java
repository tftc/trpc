package com.itiancai.trpc.springsupport.util;

import java.util.Comparator;

public class KeyComparator implements Comparator<Integer> {

    @Override
    public int compare(Integer k1, Integer k2) {
      return k2.compareTo(k1);
    }
  }