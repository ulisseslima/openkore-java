package com.dvlcube.openkore.src;

import static com.dvlcube.openkore.adaptations.PerlList.qw;

import java.util.List;

/**
 * MODULE DESCRIPTION: Message translation framework <br>
 * <br>
 * This module provides functions for translating messages in the user's <br>
 * native language. Translations are stored in <br>
 * <a href="http://www.gnu.org/software/gettext/">GNU gettext</a> translation <br>
 * files (*.mo). <br>
 * <br>
 * <b>Notes:</b> <br>
 * `l <br>
 * - Translation files MUST be encoded in UTF-8 (without BOM). <br>
 * - We use short locale names, as defined by
 * http://www.loc.gov/standards/iso639-2/php/English_list.php <br>
 * `l`
 * 
 */
public class Translation {
	public static String $_translation;
	public static List<String> EXPORT = qw("T", "TF");

	// Note: some of the functions in this module are implemented in
	// src/auto/XSTools/translation/wrapper.xs

	// //
	// boolean Translation::initDefault([String podir, String locale])
	// Ensures: Translation::T() and Translation::TF() will be usable.
	// Returns: Whether initialization succeeded. It is not fatal if
	// initialization failed: this module will automatically
	// fallback to using the original (English) strings.
	//
	// Initialize the default translation object. Translation::T() and
	// Translation::TF() will only be usable after calling this function once.
	public static boolean initDefault(Object $podir, Object $locale) {
		/*
		 * $podir = DEFAULT_PODIR if (!defined $podir); $_translation =
		 * _load(_autodetect($podir, $locale));
		 */
		boolean $_translation = true;
		return /* defined */$_translation;
	}

	// //
	// String Translation::T(String message)
	// message: The message to translate.
	// Returns: the translated message, or the original message if it cannot be
	// translated.
	// Requires: Translation::initDefault() must have been called once.
	//
	// Translate $message.
	//
	// This symbol is automatically exported.
	//
	// See also: $translation->translate() and Translation::TF()
	//
	// Example:
	// use Translation;
	// Translation::initDefault();
	// print(T("hello world\n"));
	public static String T(String string) {
		return translate(string);
	}

	// //
	// String Translation::TF(String format, ...)
	// Requires: Translation::initDefault() must have been called once; $format
	// must be encoded in UTF-8.
	// Ensures: the return value is encoded in UTF-8.
	//
	// Translate $format, and perform sprintf() formatting using the specified
	// parameters.
	// This function is just a convenient way to write:<br>
	// <code>sprintf(T($format), ...);</code>
	//
	// This symbol is automatically exported.
	//
	// Example:
	// print(TF("Go to %s for more information", $url));
	public static String TF(String format, Object... values) {
		return translate(format, values);
	}

	// //
	// String $Translation->translate(String message)
	// message: The message to translate.
	// Returns: the translated message, or the original message if it cannot be
	// translated.
	//
	// Translate $message using the translation file defined by this class.
	//
	// This function is meant for plugin developers, who have their translation
	// files
	// stored in a different folder than OpenKore's. If you want to translate
	// strings
	// in OpenKore, then you should use Translation::T() instead.
	//
	// Example:
	// my $t = new Translation;
	// print($t->translate("hello world\n"));
	public static String translate(String $message, Object... args) {
		/*
		 * my ($self, $message) = @_; _translate($self->{trans}, \$message);
		 */
		return String.format($message, args);
	}
}
