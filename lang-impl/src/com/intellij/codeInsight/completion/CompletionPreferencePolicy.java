package com.intellij.codeInsight.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.codeInsight.lookup.LookupItemPreferencePolicy;
import com.intellij.openapi.util.Key;
import com.intellij.psi.WeighingComparable;
import com.intellij.psi.WeighingService;
import com.intellij.psi.statistics.StatisticsManager;

public class CompletionPreferencePolicy implements LookupItemPreferencePolicy{
  public static final Key<WeighingComparable<LookupElement<?>, CompletionLocation>> PRESELECT_WEIGHT = Key.create("PRESELECT_WEIGHT");
  private final CompletionParameters myParameters;
  private final CompletionLocation myLocation;

  public CompletionPreferencePolicy(String prefix, final CompletionParameters parameters) {
    myParameters = parameters;
    myLocation = new CompletionLocation(myParameters.getCompletionType(), prefix, myParameters);
  }

  public CompletionType getCompletionType() {
    return myParameters.getCompletionType();
  }

  public void setPrefix(String prefix) {
    myLocation.setPrefix(prefix);
  }

  public void itemSelected(LookupItem item) {
    StatisticsManager.getInstance().incUseCount(CompletionRegistrar.STATISTICS_KEY, item, myLocation);
  }

  public Comparable[] getWeight(final LookupItem<?> item) {
    if (item.getUserData(LookupItem.WEIGHT) != null) return item.getUserData(LookupItem.WEIGHT);

    final Comparable[] result = new Comparable[]{WeighingService.weigh(CompletionRegistrar.WEIGHER_KEY, item, myLocation)};

    item.putUserData(LookupItem.WEIGHT, result);

    return result;
  }


  public int compare(final LookupItem item1, final LookupItem item2) {
    if (item1 == item2) return 0;

    double priority1 = item1.getPriority();
    double priority2 = item2.getPriority();
    if (priority1 > priority2) return -1;
    if (priority1 < priority2) return 1;

    return preselectWeigh(item2).compareTo(preselectWeigh(item1));
  }

  private WeighingComparable<LookupElement<?>, CompletionLocation> preselectWeigh(final LookupItem item) {
    WeighingComparable<LookupElement<?>, CompletionLocation> data = item.getUserData(PRESELECT_WEIGHT);
    if (data == null) {
      item.putUserData(PRESELECT_WEIGHT, data = WeighingService.weigh(CompletionRegistrar.PRESELECT_KEY, (LookupElement<?>)item, myLocation));
    }
    return data;
  }

  public static int doCompare(final double priority1, final double priority2, final Comparable[] weight1, final Comparable[] weight2) {
    if (priority1 != priority2) {
      final double v = priority1 - priority2;
      if (v > 0) return -1;
      if (v < 0) return 1;
    }

    for (int i = 0; i < weight1.length; i++) {
      final Comparable w1 = weight1[i];
      final Comparable w2 = weight2[i];
      if (w1 != null || w2 != null) {
        if (w1 == null) return 1;
        if (w2 == null) return -1;
        final int res = w1.compareTo(w2);
        if (res != 0) return -res;
      }
    }

    return 0;
  }

}
