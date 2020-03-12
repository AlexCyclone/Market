package icu.cyclone.alex;

import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Market {
	private NavigableMap<Integer, Integer> bidMap = new TreeMap();
	private NavigableMap<Integer, Integer> askMap = new TreeMap(Collections.reverseOrder());

	public Market() {
	}

	public void addBid(Integer price, Integer size) {
		if (size > 0) {
			bidMap.put(price, size + bidMap.getOrDefault(price, 0));
		}
	}

	public void addAsk(Integer price, Integer size) {
		if (size > 0) {
			askMap.put(price, size + askMap.getOrDefault(price, 0));
		}
	}

	public Map.Entry<Integer, Integer> getBestBid() {
		return bidMap.lastEntry();
	}

	public Map.Entry<Integer, Integer> getBestAsk() {
		return askMap.lastEntry();
	}

	public int getSize(int price) {
		return bidMap.getOrDefault(price, 0) > 0
				? bidMap.getOrDefault(price, 0) : askMap.getOrDefault(price, 0);
	}

	public void sell(int size) {
		reduce(bidMap, size);
	}

	public void buy(int size) {
		reduce(askMap, size);
	}

	private int reduce(NavigableMap<Integer, Integer> map, int size) {
		Map.Entry<Integer, Integer> entry = map.lastEntry();
		while (size > 0 && entry != null) {
			if (entry.getValue() > size) {
				map.put(entry.getKey(), entry.getValue() - size);
				size = 0;
			} else {
				map.remove(entry.getKey());
				size -= entry.getValue();
				entry = map.lastEntry();
			}
		}
		return size;
	}
}
