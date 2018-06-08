package com.jharter.game.collections;

import com.badlogic.gdx.ai.msg.PriorityQueue;

public class SynchronizedPriorityQueue<E extends Comparable<E>> {

	private PriorityQueue<E> queue;
	
	public SynchronizedPriorityQueue () {
		queue = new PriorityQueue<E>();
	}

	public SynchronizedPriorityQueue (int initialCapacity) {
		queue = new PriorityQueue<E>(initialCapacity);
	}
	
	public SynchronizedPriorityQueue(PriorityQueue<E> priorityQueue) {
		this.queue = priorityQueue;
	}

	public synchronized boolean getUniqueness () {
		return queue.getUniqueness();
	}

	public synchronized void setUniqueness (boolean uniqueness) {
		queue.setUniqueness(uniqueness);
	}

	public synchronized boolean add (E e) {
		return queue.add(e);
	}

	public synchronized E peek () {
		return queue.peek();
	}

	public synchronized E get (int index) {
		return queue.get(index);
	}

	public synchronized int size () {
		return queue.size();
	}

	public synchronized  void clear () {
		queue.clear();
	}

	public synchronized E poll () {
		return queue.poll();
	}

}
