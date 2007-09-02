/*
 * Copyright 2000-2005 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.psi;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

/**
 * @author ven
 */

// Intersection types arise in the process of computing lub.
public class PsiIntersectionType extends PsiType {
  private static final Logger LOG = Logger.getInstance("#com.intellij.psi.PsiIntersectionType");
  private final PsiType[] myConjuncts;

  private PsiIntersectionType(PsiType[] conjuncts) {
    LOG.assertTrue(conjuncts.length > 1);
    myConjuncts = conjuncts;
  }

  public PsiType[] getConjuncts() {
    return myConjuncts;
  }

  public String getPresentableText() {
    return myConjuncts[0].getPresentableText();
  }

  public String getCanonicalText() {
    return myConjuncts[0].getCanonicalText();
  }

  public String getInternalCanonicalText() {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < myConjuncts.length; i++) {
      buffer.append(myConjuncts[i].getInternalCanonicalText());
      if (i < myConjuncts.length - 1) buffer.append(" & ");
    }
    return buffer.toString();
  }

  public boolean isValid() {
    for (PsiType conjunct : myConjuncts) {
      if (!conjunct.isValid()) return false;
    }
    return true;
  }

  public boolean equalsToText(String text) {
    return false;
  }

  public <A> A accept(PsiTypeVisitor<A> visitor) {
    return myConjuncts[0].accept(visitor);
  }

  public GlobalSearchScope getResolveScope() {
    return myConjuncts[0].getResolveScope();
  }

  @NotNull
  public PsiType[] getSuperTypes() {
    return myConjuncts;
  }

  public static PsiType createIntersection(PsiType... conjuncts) {
    LOG.assertTrue(conjuncts.length >= 1);
    if (conjuncts.length == 1) return conjuncts[0];
    return new PsiIntersectionType(conjuncts);
  }

  public PsiType getRepresentative() {
    return myConjuncts[0];
  }

  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof PsiIntersectionType)) return false;
    final PsiType[] first = getConjuncts();
    final PsiType[] second = ((PsiIntersectionType)obj).getConjuncts();
    if (first.length != second.length) return false;
    //positional equality
    for (int i = 0; i < first.length; i++) {
      if (!first[i].equals(second[i])) return false;
    }

    return true;
  }
}
