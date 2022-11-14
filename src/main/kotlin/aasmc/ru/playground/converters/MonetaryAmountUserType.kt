package aasmc.ru.playground.converters

import aasmc.ru.playground.model.MonetaryAmount
import org.hibernate.engine.spi.SessionFactoryImplementor
import org.hibernate.metamodel.spi.ValueAccess
import org.hibernate.usertype.CompositeUserType
import org.hibernate.usertype.DynamicParameterizedType
import org.hibernate.usertype.DynamicParameterizedType.*
import java.io.Serializable
import java.math.BigDecimal
import java.util.*

open class MonetaryAmountUserType : CompositeUserType<MonetaryAmount>, DynamicParameterizedType {

    protected var convertTo: Currency = Currency.getInstance("USD")

    /**
     * Hibernate will use value equality to determine whether the value
     * was changed, and the database needs to be updated. We rely on the equality
     * routine we have already written on the <code>MonetaryAmount</code> class.
     */
    override fun equals(x: MonetaryAmount?, y: MonetaryAmount?): Boolean {
        return x === y || !(x == null || y == null) && x.equals(y)
    }

    override fun hashCode(x: MonetaryAmount): Int {
        return x.hashCode()
    }

    override fun instantiate(
        values: ValueAccess,
        sessionFactory: SessionFactoryImplementor
    ): MonetaryAmount {
        val amount = values.getValue(0, BigDecimal::class.java)
        val currency = Currency.getInstance(
            values.getValue(1, String::class.java) ?: "USD"
        )
        return MonetaryAmount(amount, currency)
    }

    /**
     * Composite user type also has to provide a way to decompose the returned type
     * into the individual components/properties of the embeddable mapper class
     * through getPropertyValue. The property index, just like in the instantiate
     * method, is based on the alphabetical order of the attribute names of the
     * embeddable mapper class.
     */
    override fun getPropertyValue(component: MonetaryAmount, property: Int): Any? {
        return when (property) {
            0 -> component.value
            1 -> component.currency.currencyCode
            else -> null
        }
    }

    override fun embeddable(): Class<*> {
        return MonetaryAmountEmbeddable::class.java
    }

    /**
     * The method <code>returnedClass</code> adapts the given class, in this case
     * <code>MonetaryAmount</code>.
     */
    override fun returnedClass(): Class<MonetaryAmount> {
        return MonetaryAmount::class.java
    }

    /**
     * If Hibernate has to make a copy of the value, it will call
     * this method. For simple immutable classes like <code>MonetaryAmount</code>,
     * you can return the given instance.
     */
    override fun deepCopy(value: MonetaryAmount): MonetaryAmount {
        return value
    }

    /**
     * Hibernate can enable some optimizations if it knows
     * that <code>MonetaryAmount</code> is immutable.
     */
    override fun isMutable(): Boolean {
        return false
    }

    /**
     * Hibernate calls <code>disassemble</code> when it stores a value in the global shared second-level
     * cache. You need to return a <code>Serializable</code> representation. For <code>MonetaryAmount</code>,
     * a <code>String</code> representation is an easy solution. Or, because <code>MonetaryAmount</code> is actually
     * <code>Serializable</code>, you could return it directly.
     */
    override fun disassemble(value: MonetaryAmount): Serializable {
        return value.toString()
    }

    /**
     * Hibernate calls this method when it reads the serialized
     * representation from the global shared second-level cache. We create a
     * <code>MonetaryAmount</code> instance from the <code>String</code>
     * representation. Or, if have stored a serialized <code>MonetaryAmount</code>,
     * you could return it directly.
     */
    override fun assemble(cached: Serializable, owner: Any?): MonetaryAmount {
        return MonetaryAmount.fromString(cached as String)
    }

    /**
     * Called during <code>EntityManager#merge()</code> operations, you
     * need to return a copy of the <code>original</code>. Or, if your value type is
     * immutable, like <code>MonetaryAmount</code>, you can simply return the original.
     */
    override fun replace(detached: MonetaryAmount, managed: MonetaryAmount, owner: Any?): MonetaryAmount {
        return detached
    }

    /**
     * You can access some dynamic parameters here, such as the
     * name of the mapped columns, the mapped (entity) table, or even the
     * annotations on the field/getter of the mapped property. We won't need
     * them in this example though.
     */
    override fun setParameterValues(parameters: Properties) {
        val parameterType = parameters[PARAMETER_TYPE] as ParameterType
        val columns = parameterType.columns
        val table = parameterType.table
        val annotations = parameterType.annotationsMethod

        /**
         * We only use the <code>convertTo</code> parameter to
         * determine the target currency when saving a value into the database.
         * If the parameter hasn't been set, we default to US Dollar.
         */
        val convertToParameter = parameters.getProperty("convertTo")
        this.convertTo = Currency.getInstance(convertToParameter ?: "USD")
    }

    open class MonetaryAmountEmbeddable {
        var amount: BigDecimal = BigDecimal.ZERO
        var currency: String = "USD"
    }
}



















