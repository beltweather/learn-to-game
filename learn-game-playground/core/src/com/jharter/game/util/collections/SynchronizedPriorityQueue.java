package com.jharter.game.util.collections;

import com.badlogic.gdx.ai.msg.PriorityQueue;

public class SynchronizedPriorityQueue<E extends Comparable<E>> {

	private PriorityQueue<E> queueA;
	private PriorityQueue<E> queueB;
	private boolean useA = true;
	
	public SynchronizedPriorityQueue () {
		queueA = new PriorityQueue<E>();
		queueB = null;
	}

	public SynchronizedPriorityQueue (int initialCapacity) {
		queueA = new PriorityQueue<E>(initialCapacity);
		queueB = null;
	}
	
	public SynchronizedPriorityQueue(PriorityQueue<E> priorityQueue) {
		this.queueA = priorityQueue;
		this.queueB = null;
	}

	public synchronized boolean getUniqueness () {
		return queue().getUniqueness();
	}

	public synchronized void setUniqueness (boolean uniqueness) {
		queue().setUniqueness(uniqueness);
	}

	public synchronized boolean add (E e) {
		return queue().add(e);
	}

	public synchronized E peek () {
		return queue().peek();
	}

	public synchronized E get (int index) {
		return queue().get(index);
	}

	public synchronized int size () {
		return queue().size();
	}

	public synchronized  void clear () {
		queue().clear();
	}

	public synchronized E poll () {
		return queue().poll();
	}
	
	public synchronized PriorityQueue<E> consume() {
		useA = !useA;
		queue().clear();
		return otherQueue();
	}
	
	private PriorityQueue<E> queue() {
		if(useA) {
			return queueA;
		} else if(queueB == null) {
			queueB = new PriorityQueue<E>(queueA.size());
		}
		return queueB;
	}
	
	private PriorityQueue<E> otherQueue() {
		if(!useA) {
			return queueA;
		} else if(queueB == null) {
			queueB = new PriorityQueue<E>(queueA.size());
		}
		return queueB;
	}

}
