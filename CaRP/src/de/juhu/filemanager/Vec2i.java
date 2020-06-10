package de.juhu.filemanager;

import java.io.Serializable;

/**
 * Representiert einen zweidimensionalen Vektor
 * 
 * @category Math
 * @version 0.1
 * @author Juhu1705
 */
public class Vec2i implements Serializable {

	/**
	 * the x, y coordinates of the Vec2i
	 */
	public int x, y;

	/**
	 * Creates a Null-Vector
	 */
	public Vec2i() {
		this.x = 0;
		this.y = 0;
	}

	/**
	 * x, y coordinates set to f
	 * 
	 * @param f A int value
	 */
	public Vec2i(int f) {
		this.x = f;
		this.y = f;
	}

	/**
	 * Sets x, y to x, y
	 * 
	 * @param x
	 * @param y
	 */
	public Vec2i(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Creates a new Vec2i with the values of the given Vec2i
	 * 
	 * @param Vec2i A Vec2i
	 */
	public Vec2i(Vec2i Vec2i) {
		this.x = Vec2i.x;
		this.y = Vec2i.y;
	}

	/**
	 *
	 * @return Length of the vector
	 */
	public int getLength() {
		return (int) Math.sqrt(x * x + y * y);
	}

	/**
	 * Normalize the vector
	 * 
	 * @return this
	 */
	public Vec2i normalize() {
		int length = this.getLength();

		this.x /= length;
		this.y /= length;

		return this;
	}

	/**
	 * Multiplies this vector and the given Vec2i.
	 * 
	 * @param Vec2i The vector to multiply this vector with.
	 * @return A new Vec2i with the multiplied data.
	 */
	public Vec2i mul(Vec2i Vec2i) {
		return new Vec2i(x * Vec2i.x, y * Vec2i.y);
	}

	/**
	 * Multiplies this vector with the int f.
	 * 
	 * @param f The int to multiply this vector with.
	 * @return A new Vec2i with the multiplied data.
	 */
	public Vec2i mul(int f) {
		return new Vec2i(x * f, y * f);
	}

	/**
	 * Divided this with given Vec2i
	 * 
	 * @param Vec2i The Vec2i to divide this Vec2i with
	 * @return A new Vec2i with divided values
	 */
	public Vec2i div(Vec2i Vec2i) {
		return new Vec2i(x / Vec2i.x, y / Vec2i.y);
	}

	/**
	 * Divided this with given int
	 * 
	 * @param f The int to divide this Vec2i with
	 * @return A new Vec2i with divided values
	 */
	public Vec2i div(int f) {
		return new Vec2i(x / f, y / f);
	}

	/**
	 * Subtract given Vec2i from this
	 * 
	 * @param Vec2i The Vec2i to subtract this Vec2i with
	 * @return A new Vec2i with subtracted values
	 */
	public Vec2i sub(Vec2i Vec2i) {
		return new Vec2i(x - Vec2i.x, y - Vec2i.y);
	}

	/**
	 * Subtract given int from this
	 * 
	 * @param f The int to subtract this Vec2i with
	 * @return A new Vec2i with subtracted values
	 */
	public Vec2i sub(int f) {
		return new Vec2i(x - f, y - f);
	}

	/**
	 * Add given Vec2i to this
	 * 
	 * @param Vec2i The Vec2i to add to this Vec2i
	 * @return A new Vec2i with added values
	 */
	public Vec2i add(Vec2i Vec2i) {
		return new Vec2i(x + Vec2i.x, y + Vec2i.y);
	}

	/**
	 * Add given int to this
	 * 
	 * @param f The int to add to this Vec2i
	 * @return A new Vec2i with added values
	 */
	public Vec2i add(int f) {
		return new Vec2i(x + f, y + f);
	}

	/**
	 * Returns modulo
	 * 
	 * @param Vec2i The Vec2i to modulo this Vec2i with
	 * @return A new Vec2i with modulo values
	 */
	public Vec2i mod(Vec2i Vec2i) {
		return new Vec2i(x % Vec2i.x, y % Vec2i.y);
	}

	/**
	 * Returns modulo
	 * 
	 * @param f The int to modulo this Vec2i with
	 * @return A new Vec2i with modulo values
	 */
	public Vec2i mod(int f) {
		return new Vec2i(x % f, y % f);
	}

	/**
	 * Sets the x and y value
	 * 
	 * @param x
	 * @param y
	 */
	public Vec2i set(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Vec2i ? this.x == ((Vec2i) obj).x && ((Vec2i) obj).y == this.y : super.equals(obj);
	}

	@Override
	protected Vec2i clone() throws CloneNotSupportedException {
		return new Vec2i(this);
	}

	@Override
	public String toString() {
		return "[" + x + "|" + y + "]";
	}
}
