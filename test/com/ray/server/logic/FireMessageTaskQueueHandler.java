package com.ray.server.logic;


public interface FireMessageTaskQueueHandler {
	
	/**
     * Returns <tt>true</tt> if and only if the specified <tt>event</tt> is
     * allowed to be offered to the event queue.  The <tt>event</tt> is dropped
     * if <tt>false</tt> is returned.
     */
	boolean accept(Object source, FireMessageTask task);

    /**
     * Invoked after the specified <tt>event</tt> has been offered to the
     * event queue.
     */
    void offered(Object source, FireMessageTask task);

    /**
     * Invoked after the specified <tt>event</tt> has been polled from the
     * event queue.
     */
    void polled(Object source, FireMessageTask task);
}
