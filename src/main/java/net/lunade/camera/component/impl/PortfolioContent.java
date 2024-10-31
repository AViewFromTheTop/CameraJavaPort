package net.lunade.camera.component.impl;

import java.util.List;

public interface PortfolioContent<T, C> {
	List<T> pages();

	C withReplacedPages(List<T> pages);
}
