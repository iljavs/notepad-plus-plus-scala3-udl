// A highlighting sample for the Scala 3 User Defined Language in this repository.
// Open it with the Scala 3 UDL selected to see everything the definition styles at once.
// Apostrophes in comments are handled, so don't expect this line to break the ones below it.

/* A block comment,
   /* holding a nested block comment, */
   which Scala permits and which this definition handles. */

package example.scala3

import scala.annotation.tailrec
import scala.collection.immutable.{ArraySeq as Immutable}

// -- declarations, modifiers, built-in types ---------------------------------

sealed abstract class Shape:
  def area: Double
end Shape

final case class Circle(radius: Double) extends Shape derives CanEqual:
  override def area: Double = math.Pi * radius * radius

open class Counter(private var n: Int):
  def bump(): Unit = n += 1
  def value: Int = n

trait Describable[A]:
  def describe(a: A): String

enum Colour(val code: Int):
  case Red   extends Colour(0xFF0000)
  case Green extends Colour(0x00FF00)
  case Blue  extends Colour(0x0000FF)

opaque type Metres = Double

object Metres:
  def apply(d: Double): Metres = d
  extension (m: Metres) def toDouble: Double = m

object Facade:
  val counter = Counter(0)
  export counter.{bump, value}

type Both[A] = (A, A)

type Elem[X] = X match
  case String  => Char
  case Immutable[t] => t

// -- given, using, extension, inline -----------------------------------------

given describableCircle: Describable[Circle] =
  (c: Circle) => s"circle of radius ${c.radius}"

lazy val anonymous = new Describable[String]:
  def describe(a: String): String = a

def render[A](a: A)(using d: Describable[A]): String = d.describe(a)

extension (s: String)
  def shout: String = s.toUpperCase + "!"
  infix def joinedWith(other: String): String = s + " " + other

inline def twice(inline n: Int): Int = n + n

transparent inline def widened(n: Int): Long = n.toLong

extension (a: Int) def ⊕ (b: Int): Int = a + b

// -- control flow -------------------------------------------------------------

def classify(n: Int): String =
  if n < 0 then "negative"
  else if n == 0 then "zero"
  else "positive"

def describeColour(c: Colour): String = c match
  case Colour.Red   => "warm"
  case Colour.Green => "calm"
  case Colour.Blue  => "cool"

@tailrec
def sumTo(n: Int, acc: Long = 0L): Long =
  if n <= 0 then acc else sumTo(n - 1, acc + n)

def evenSquares(xs: List[Int]): List[Int] =
  for
    x <- xs
    if x % 2 == 0
  yield x * x

def countdown(from: Int): Unit =
  var i = from
  while i > 0 do
    i -= 1

def clamp(n: Int): Int =
  if n < 0 then return 0
  if n > 100 then return 100
  n

def readOrDefault(load: () => String): String =
  try load()
  catch
    case _: IllegalStateException => "unavailable"
    case e: RuntimeException      => throw e
  finally
    ()

// -- strings and interpolation ------------------------------------------------

val plain        = "a plain string, in which $plain is not interpolated"
val interpolated = s"one plus one is ${1 + 1}, and nesting works: ${classify(0)}"
val formatted    = f"pi to three places is ${math.Pi}%2.3f"
val rawText      = raw"a raw string, in which \n stays two characters"
val endsInQuote  = """this string ends with a quote""""

val block =
  """A triple-quoted string
    |spanning several lines.
    |""".stripMargin

val blockInterpolated =
  s"""Colour ${Colour.Red} has code ${Colour.Red.code},
     |and 0xFF is ${0xFF}.
     |""".stripMargin

val `reserved name` = 1
val `type`          = "a backtick-quoted identifier"

// -- character literals -------------------------------------------------------
// Escapes, digits, the double quote and the backtick are coloured. A literal
// holding a plain letter, a space or ordinary punctuation is the known gap:
// see "Known limitations" in the README.

val newline   = '\n'
val tab       = '\t'
val quote     = '\''
val backslash = '\\'
val zero      = '0'
val dquote    = '"'
val letter    = 'a'
val space     = ' '
val plus      = '+'

// -- numbers ------------------------------------------------------------------

val decimal  = 42
val hex      = 0xCAFE
val binary   = 0b0010_0000
val grouped  = 1_000_000
val long     = 42L
val float    = 1.5f
val double   = 2.0d
val exponent = 1.5e-3
val negative = -2.5E+10

// -- type-level operators -----------------------------------------------------

type Identity = [X] =>> X
type Contextual = Int ?=> String

def widen[A, B >: A](a: A): B = a
def narrow[A, B <: A](b: B): A = b
def byEquality[A, B](using ev: A =:= B)(a: A): B = ev(a)
def bySubtype[A, B](using ev: A <:< B)(a: A): B = ev(a)

val consed = 1 :: 2 :: Nil

// -- macro quotation ----------------------------------------------------------
// '{ ... } is a quote and ${ ... } a splice. Neither is read as a character
// literal, which is the trap most hand-written highlighters fall into.

import scala.quoted.*

inline def inspect(inline value: Int): String = ${ inspectImpl('value) }

private def inspectImpl(value: Expr[Int])(using Quotes): Expr[String] =
  '{ "value = " + ${ value }.toString }

// -- entry point --------------------------------------------------------------

@main def demo(): Unit =
  println(render(Circle(2.0)))
  println(classify(-1) joinedWith "then" joinedWith classify(1))
  println("quiet".shout)
  println(1 ⊕ 2)
  println(evenSquares(List(1, 2, 3, 4)))
end demo
