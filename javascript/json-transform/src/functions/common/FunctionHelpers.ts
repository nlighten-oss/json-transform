import BigNumber from "bignumber.js";

export const BigDecimal = BigNumber.clone({
  // We try to fit into Decimal128 which supports 34 decimal digits of precision
  // so in principle, we allow 19 digits for the whole number and 15 for the fraction
  ROUNDING_MODE: BigNumber.ROUND_HALF_UP,
  DECIMAL_PLACES: 15
});
