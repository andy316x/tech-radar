package com.ai.techrader.mocked.drupal.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.ai.techrader.mocked.drupal.DrupalTechnology;
import com.ai.techrader.mocked.drupal.FakeDrupalTechnologyService;

/**
 * Provides the implementation to allow a list of technologies to be retrieved from a file on the file system (instead of from Drupal).
 */
public class FakeDrupalTechnologyServiceImpl implements FakeDrupalTechnologyService
{
	private static final String TECHNOLOGIES_FILE = "technologies.xml";

	@Override
	public List<DrupalTechnology> getTechnologies()
	{
		final List<DrupalTechnology> technologies = new ArrayList<DrupalTechnology>();

		final SAXBuilder builder = new SAXBuilder();

		final ClassLoader classLoader = getClass().getClassLoader();
		final File xmlFile = new File(classLoader.getResource(TECHNOLOGIES_FILE).getFile());

		try
		{
			final Document document = builder.build(xmlFile);
			final Element rootNode = document.getRootElement();
			final List<?> list = rootNode.getChildren("technology");

			for (int i = 0; i < list.size(); i++)
			{
				final Element node = (Element) list.get(i);

				final DrupalTechnology technology = new DrupalTechnology();
				technology.setName(node.getChildText("name"));
				technology.setDescription(node.getChildText("description"));

				technologies.add(technology);
			}
		}
		catch (final IOException e)
		{
			throw new IllegalStateException("Unable to read the file " + TECHNOLOGIES_FILE, e);
		}
		catch (final JDOMException e)
		{
			throw new IllegalStateException("Unable to parse XML in file " + TECHNOLOGIES_FILE, e);
		}

		return technologies;
	}
}
