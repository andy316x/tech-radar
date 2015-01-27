package com.ai.techrader.mocked.drupal;

import java.util.List;

/**
 * Service to allow a list of technologies to be retrieved from a file on the file system (instead of from Drupal).
 */
public interface FakeDrupalTechnologyService
{
	/**
	 * <p>
	 * Returns the list of technologies retrieved from a file on the file system.
	 * </p>
	 * <p>
	 * If the file does not exist or is empty, an empty list is returned.
	 * </p>
	 *
	 * @return list of technologies
	 */
	List<DrupalTechnology> getTechnologies();
}
