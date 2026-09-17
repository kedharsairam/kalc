package com.kraft.calculator.domain

import kotlin.math.pow

data class EmiResult(
    val emi: Double,
    val totalInterest: Double,
    val totalPayment: Double,
)

data class GstResult(
    val tax: Double,
    val gross: Double,
    val net: Double,
    val cgst: Double,
    val sgst: Double,
    val igst: Double,
)

object FinanceCalculators {

    /**
     * Standard amortizing loan EMI.
     * EMI = P * r * (1+r)^n / ((1+r)^n - 1), r = annual%/12.
     */
    fun emi(principal: Double, annualRatePct: Double, months: Int): EmiResult {
        require(principal >= 0 && months > 0)
        if (annualRatePct <= 0) {
            val emi = principal / months
            return EmiResult(emi, 0.0, principal)
        }
        val r = annualRatePct / 100.0 / 12.0
        val factor = (1 + r).pow(months)
        val emi = principal * r * factor / (factor - 1)
        val total = emi * months
        return EmiResult(emi, total - principal, total)
    }

    /**
     * India GST calculation.
     * Forward: given net, compute tax + gross.
     * Reverse: given gross, compute net + tax.
     * Intra-state splits tax into CGST=SGST=tax/2, else all IGST.
     */
    fun gst(
        amount: Double,
        ratePct: Double,
        isForward: Boolean = true,
        intraState: Boolean = true,
    ): GstResult {
        require(amount >= 0 && ratePct >= 0)
        val (net, tax, gross) = if (isForward) {
            val t = amount * ratePct / 100.0
            Triple(amount, t, amount + t)
        } else {
            val n = amount / (1 + ratePct / 100.0)
            Triple(n, amount - n, amount)
        }
        val (cgst, sgst, igst) = if (intraState) {
            Triple(tax / 2, tax / 2, 0.0)
        } else {
            Triple(0.0, 0.0, tax)
        }
        return GstResult(tax, gross, net, cgst, sgst, igst)
    }
}
