package com.irpgeditor.irpgeditor.event;

/*
 * Copyright:    Copyright (c) 2004
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software; see the file COPYING.  If not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 */

/**
 * Receives the source as it is being loaded.
 * 
 * @author Derek Van Kooten.
 */
public interface SourceLoader {
  /**
   * Gets called for every line that is loaded from the source member.
   * 
   * @param number float The number of the line in the source member.
   * @param date int The last changed date of the line in the source member.
   * @param line String The actual source code of the line.
   */
  public void lineLoaded(float number, int date, String line);
}
