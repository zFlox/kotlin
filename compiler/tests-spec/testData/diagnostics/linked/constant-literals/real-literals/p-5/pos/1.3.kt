// !CHECK_TYPE

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: constant-literals, real-literals -> paragraph 5 -> sentence 1
 * NUMBER: 3
 * DESCRIPTION: A type checking of a real literal with omitted a whole-number part.
 */

// TESTCASE NUMBER: 1
val value_1 = .0 checkType { _<Double>() }

// TESTCASE NUMBER: 2
val value_2 = .00 checkType { _<Double>() }

// TESTCASE NUMBER: 3
val value_3 = .000 checkType { _<Double>() }

// TESTCASE NUMBER: 4
val value_4 = .0000 checkType { _<Double>() }

// TESTCASE NUMBER: 5
val value_5 = .1234567890 checkType { _<Double>() }

// TESTCASE NUMBER: 6
val value_6 = .23456789 checkType { _<Double>() }

// TESTCASE NUMBER: 7
val value_7 = .345678 checkType { _<Double>() }

// TESTCASE NUMBER: 8
val value_8 = .4567 checkType { _<Double>() }

// TESTCASE NUMBER: 9
val value_9 = .56 checkType { _<Double>() }

// TESTCASE NUMBER: 10
val value_10 = .65 checkType { _<Double>() }

// TESTCASE NUMBER: 11
val value_11 = .7654 checkType { _<Double>() }

// TESTCASE NUMBER: 12
val value_12 = .876543 checkType { _<Double>() }

// TESTCASE NUMBER: 13
val value_13 = .98765432 checkType { _<Double>() }

// TESTCASE NUMBER: 14
val value_14 = .0987654321 checkType { _<Double>() }

// TESTCASE NUMBER: 15
val value_15 = .1111 checkType { _<Double>() }

// TESTCASE NUMBER: 16
val value_16 = .22222 checkType { _<Double>() }

// TESTCASE NUMBER: 17
val value_17 = .33333 checkType { _<Double>() }

// TESTCASE NUMBER: 18
val value_18 = .444444 checkType { _<Double>() }

// TESTCASE NUMBER: 19
val value_19 = .5555555 checkType { _<Double>() }

// TESTCASE NUMBER: 20
val value_20 = .66666666 checkType { _<Double>() }

// TESTCASE NUMBER: 21
val value_21 = .777777777 checkType { _<Double>() }

// TESTCASE NUMBER: 22
val value_22 = .8888888888 checkType { _<Double>() }

// TESTCASE NUMBER: 23
val value_23 = .99999999999 checkType { _<Double>() }

// TESTCASE NUMBER: 24
val value_24 = .00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001 checkType { _<Double>() }

// TESTCASE NUMBER: 25
val value_25 = <!FLOAT_LITERAL_CONFORMS_ZERO!>.000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001<!> checkType { _<Double>() }

// TESTCASE NUMBER: 26
val value_26 = .0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000123456789012345678901234567890123456789012345678901234567890 checkType { _<Double>() }

// TESTCASE NUMBER: 27
val value_27 = .44444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444441 checkType { _<Double>() }

// TESTCASE NUMBER: 28
val value_28 = .777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777771 checkType { _<Double>() }

// TESTCASE NUMBER: 29
val value_29 = .9999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999123456789912345678991234567899123456789912345678991234567899 checkType { _<Double>() }