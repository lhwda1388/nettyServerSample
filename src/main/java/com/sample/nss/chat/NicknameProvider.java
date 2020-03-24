package com.sample.nss.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class NicknameProvider {
	private final Queue<String> pool;
    private final Set<String> preset;
    private final Set<String> occupied = new HashSet<>();

    public NicknameProvider() {
    	/*
        List<String> names = Arrays.asList(
                "Mark", "Tim", "Evan", "Bill", "Larry",
                "Paul", "Eric", "David", "Martin", "Matz",
                "Rich", "John", "Rob", "Ken", "Joe",
                "Simon", "Roberto", "Niklaus", "Alan", "Richard",
                "James", "Kyrie", "Michale", "Stephen", "Derrik",
                "Kevin", "Russel", "LeBron", "Kobe", "Chris",
                "Tony", "Blake", "Dwayne", "Carmelo"
        );
        */
        ArrayList<String> names = new ArrayList<String>();
        for (int i = 0; i < 10000; i++) {
        	names.add("name" + i);
        }
        
        preset = new HashSet<>(names);
        Collections.shuffle(names);
        pool = new LinkedList<>(names);
    }

    public synchronized boolean available(String nickname) {
        return !preset.contains(nickname) && !occupied.contains(nickname);
    }

    public synchronized String reserve() {
        String n = pool.poll();
        if (n != null) occupied.add(n);
        return n;
    }

    public synchronized void reserve(String custom) {
        if (!available(custom)) throw new RuntimeException("not available name");
        occupied.add(custom);
    }

    public synchronized NicknameProvider release(String nick) {
        occupied.remove(nick);
        if (preset.contains(nick) && !pool.contains(nick)) pool.add(nick);
        return this;
    }
}
